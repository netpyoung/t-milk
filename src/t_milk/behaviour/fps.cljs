(ns t-milk.behaviour.fps
  (:require-macros
   [milk.macro :refer [def-behaviour letc]])

  (:require
   [js.canvas.core :as js.canvas]
   [milk.gatom :as gatom]
   [milk.event :as event]))


(def-behaviour fps []

  [fps* (atom -1)
   frame-acc* (atom 0)
   dt-acc* (atom 0)]

  :order 1

  :update
  (fn [ga dt]
    (swap! frame-acc* inc)
    (swap! dt-acc* + dt)
    (when (>= @dt-acc* 1000)
      (let [fps (* 1000 (/ @frame-acc* @dt-acc*))
            fps' (-> fps (* 10) js/Math.round (/ 10) float)]
        (reset! fps* fps')
        (reset! frame-acc* 0)
        (reset! dt-acc* 0))))

  :render
  (fn [ga ctx]
    (letc ga [transform :transform]
          (let [[tx ty] (aget transform :pos)]
            (-> ctx
                (js.canvas/save)
                (js.canvas/font-style "30pt Arial")
                (js.canvas/fill-style :#FF0000)
                (js.canvas/text {:x tx :y ty :text @fps*})
                (js.canvas/restore))))))
