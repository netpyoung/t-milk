(ns milk.network.websocket
  (:require [sonet.websocket :as ws]
            ))

(def ^:private channel-dic*
  (atom {}))


(def ^:private buffer-dic*
  (atom {}))


(defn- register [channel-k type val]
  (let [buff (channel-k @buffer-dic*)]
    (swap! buff conj {:type type
                      :val val})))


(defn connect [channel-info]
  (let [[channel-k url] channel-info
        sock (ws/open url)]

    (swap! channel-dic* assoc channel-k sock)
    (swap! buffer-dic* assoc channel-k (atom []))

    (-> sock
        (ws/on-open    #(register channel-k :open nil))
        (ws/on-message #(register channel-k :receive (-> %
                                                         .-data
                                                         str
                                                         cljs.reader/read-string)))
        (ws/on-error #(register channel-k :error nil))

        ;; TODO(kep) close시 dissoc시켜버리자.
        (ws/on-close #(register channel-k :close nil)))))


(defn close [channel-info]
  (let [[channel-k url] channel-info]
    (swap! channel-dic* update-in [channel-k] (fn [sock] (ws/close sock)))
    (swap! channel-dic* dissoc channel-k )))


(defn send [channel-info msg]
  (let [[channel-k url] channel-info]
    (when-let [sock (channel-k @channel-dic*)]
      (ws/send sock msg))))


(defn dequeue [channel-info]

  (let [[channel-k url] channel-info
        buff (channel-k @buffer-dic*)]

    (when-let [p (peek @buff)]
      (swap! buff pop)

      (when-not (channel-k @channel-dic*)
        (swap! buffer-dic* dissoc channel-k))
      p)))


(defn clear-buffer []
  )


;; TODO(kep) websocket disconnect 처리.
