(ns milk.gameloop
  (:require [milk.time :as time]
            [milk.event-manager :as event-manager]
            [milk.input :as input]
            [milk.gatom :as gatom]
            [milk.scene :as scene]

            [milk.screen.canvas :as screen.canvas]
            [milk.exception :as exception]

            ;; system.
            [milk.system.behaviour :as system.behaviour]
            [milk.system.transform :as system.transform]
            [milk.system.box-collider :as system.box-collider]
            ))


(def ^:const DESIRED_FRAME_RATE 40)
(def ^:const DESIRED_TIME (/ 1000 (inc DESIRED_FRAME_RATE)))


(def ^:private activate* (atom false))


(defn- process-gameloop-events []
  (doseq [[k events] (group-by :info (event-manager/get-events :gameloop))]
    (case k
      :load-level
      (doseq [evt (distinct events)]
        (let [[scene-k args] (:val evt)]
          (gatom/clear!)
          (scene/load-scene scene-k args)))

      nil)))


(defn- process-gatom-events []
  (doseq [[k events] (group-by :info (event-manager/get-events :gatom))]
    (case k
      :register
      (doseq [evt (distinct events)]
        (let [ga (:val evt)]
          (doseq [comp (gatom/get-components ga :behaviour)]
            (when-let [on-awake (aget comp :on-awake)]
              (on-awake ga)))
          (gatom/activate! ga)))

      :destroy
      (doseq [evt (distinct events)]
        (let [ga (:val evt)]
          (doseq [comp (gatom/get-components ga :behaviour)]
            (when-let [on-destroy (aget comp :on-destroy)]
              (on-destroy ga)))
          (gatom/unregister! ga)))
      nil)))


(defn- process-component-events []
  (doseq [[k events] (group-by :info (event-manager/get-events :component))]

    (case k
      :add
      (doseq [evt events]
        (let [[ga c] (:val evt)]
          (try
            (do
              (gatom/add-c! ga c)
              (when-let [on-awake (aget c :on-awake)]
                (if (aget ga "live?")
                  (on-awake ga))))
            (catch js/Error err
              (exception/err-handle :ga ga
                                    :info "process-componenet-events :: add"
                                    :err err)))))

      :destroy
      (doseq [evt events]
        (let [[ga c] (:val evt)]
          (try
            (when ga
              (when-let [on-destroy (aget c :on-destroy)]
                (on-destroy ga))
              (gatom/del-c! ga c))
            (catch js/Error err
              (exception/err-handle :ga ga
                                    :info "process-componenet-events :: destroy"
                                    :err err)))))

      :send-message
      (doseq [evt events]
        (try
          (let [[c k args] (:val evt)]
            (when c
              (when-let [k-fn (aget c k)]
                (if args
                  (apply k-fn args)
                  (k-fn)))))
          (catch js/Error err
            (exception/err-handle :info "process-componenet-events :: send-message"
                                  :err err))))
      nil)))




(defn- game-loop [f]
  (let [revise-time (+ DESIRED_TIME (js/Date.now))]
    (screen.canvas/save!)
    (screen.canvas/clear!)

    (event-manager/flush!)

    ;; process gameloop events.
    (process-gameloop-events)

    ;; process input events.
    (input/update! (event-manager/get-events :mouse)
                   (event-manager/get-events :keyboard))

    ;; process gatom events.
    (process-gatom-events)

    ;; process component events.
    (process-component-events)

    ;; update logic.
    (when f
      (f))

    (system.box-collider/update!)

    (system.behaviour/update!)
    (system.behaviour/render!)

    ;; for debug.
    ;; (system.transform/debug-render!)
    ;; (system.box-collider/debug-render!)

    (screen.canvas/restore!)

    (time/update!)
    (when @activate*
      (js/setTimeout #(game-loop f) (max 0 (- revise-time (js/Date.now)))))))


;; =============
;; publics.

(defn init! [level-dic]
  (input/init! :#milk-root)
  (screen.canvas/init! :#milk-canvas)
  (scene/init! level-dic)
  (scene/load-scene :default))


(defn start!
  ([]
     (start! nil))
  ([f]
     (reset! activate* true)
     (time/init!)
     (try
       (game-loop f)
       (catch js/Error err
         (exception/err-handle :info "GAME-LOOP EXCEPTION CATCH" :err err)))))


(defn stop! []
  (reset! activate* false))
