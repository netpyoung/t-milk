(ns milk.system.transform
  (:require-macros [milk.macro :refer [def-render-system]])
  (:require [milk.gatom :as gatom]
            [milk.time :as time]
            [milk.screen.canvas :as screen.canvas]
            [js.canvas.core :as js.canvas]))


(def-render-system debug-render! :transform
  [ga comp]

  (let [[tx ty] (aget comp :pos)]
    (-> ctx
        (js.canvas/save)
        (js.canvas/fill-style :#FF00FF)
        (js.canvas/fill-rect {:x tx :y ty :w 10 :h 10})
        (js.canvas/restore))))
