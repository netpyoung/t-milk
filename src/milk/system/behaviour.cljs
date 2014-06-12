(ns milk.system.behaviour
  (:require-macros [milk.macro :refer [def-system
                                       def-render-system]])
  (:require [milk.gatom :as gatom]
            [milk.time :as time]
            [milk.screen.canvas :as screen.canvas]
            [milk.exception :as exception]))


(def-system update! :behaviour
  [ga comp]
  (when-let [update (aget comp :update)]
    (update ga dt)))


(def-render-system render! :behaviour
  [ga comp]
  (when-let [render (aget comp :render)]
    (try
      (render ga ctx)
      (catch js/Error err
        (exception/err-handle :ga ga
                              :info "render ga ERROR"
                              :err err)))))
