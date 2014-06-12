(ns sonet.websocket)

;; refs :
;;  - http://dev.w3.org/html5/websockets/
;;  - https://developer.mozilla.org/en-US/docs/WebSockets/
;;  - http://www.iana.org/assignments/websocket/websocket.xml

(defn open [url]
  (when-let [socket (js/WebSocket. url)]
    socket))


(defn send [socket message]
  (when socket
    (.send socket message)))


(defn close [socket]
  (when socket
    (.close socket)))


;; receive handler.
(defn on-open [socket handler]
  (when socket
    (aset socket "onopen" handler)
    socket))


(defn on-close [socket handler]
  (when socket
    (aset socket "onclose" handler)
    socket))


(defn on-message [socket handler]
  (when socket
    (aset socket "onmessage" handler)
    socket))


(defn on-error [socket handler]
  (when socket
    (aset socket "onerror" handler)
    socket))
