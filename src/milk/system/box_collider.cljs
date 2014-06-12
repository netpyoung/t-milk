(ns milk.system.box-collider
  (:require-macros [milk.macro :refer [def-system def-render-system letc]])
  (:require [milk.gatom :as gatom]
            [milk.time :as time]
            [milk.screen.canvas :as screen.canvas]
            [milk.event :as event]
            [js.canvas.core :as js.canvas]
            [milk.input.mouse :as input.mouse]))


(defn- rect-in [[x y w h] [px py]]
  (and (<= x px (+ x w))
       (<= y py (+ y h))))


(defn- get-boundary [ga box-collider]
  (letc ga [transform :transform]
        (let [[tx ty] (aget transform :pos)
              bw (aget box-collider :w)
              bh (aget box-collider :h)]
          [tx ty bw bh])))


(def ^:private dragging-target* (atom nil))


(defn- ordering-collider [box-colliders]
  (->> box-colliders
       (sort (fn [[_ x] [_ y]] (> (aget x :z) (aget y :z))))))


(defn- ray-box-collider [mouse-pos box-collliders]
  (->> box-collliders
       (filter (fn [[ga comp]]
                 (rect-in (get-boundary ga comp) mouse-pos)))
       (ordering-collider)
       (first)))


(defn- get-entered-collider [box-colliders]
  (->> box-colliders
       (filter (fn [[ga c]] (aget c :entered)))
       (first)))


(defn update! []

  ;; for on-drag.
  (when-let [[ga comp] @dragging-target*]
    (letc ga [behaviour :behaviour]
          (if (input.mouse/get-mouse-button 0)
            (event/send-message! behaviour :on-mouse-drag ga true)
            (do (event/send-message! behaviour :on-mouse-drag ga false)
                (reset! dragging-target* nil)))))

  (let [box-colliders (gatom/all-ga :box-collider)
        mouse-pos (input.mouse/mouse-pos)
        entered (get-entered-collider box-colliders)
        rayed (ray-box-collider mouse-pos box-colliders)]

    (if-not entered

      (when-let [[ga comp] rayed]
        (letc ga [behaviour :behaviour]
              (event/send-message! behaviour :on-mouse-enter)
              (aset comp :entered true)))

      (cond (nil? rayed)
            (let [[ga comp] entered]
              (letc ga [behaviour :behaviour]
                    (event/send-message! behaviour :on-mouse-exit)
                    (aset comp :entered false)))

            (= entered rayed)
            (let [[ga comp] entered]
              (letc ga [behaviour :behaviour]
                    (cond (input.mouse/get-mouse-button 0 :up)
                          (event/send-message! behaviour :on-mouse-up)

                          (input.mouse/get-mouse-button 0 :down)
                          (do (reset! dragging-target* entered)
                              (event/send-message! behaviour :on-mouse-down))

                          :else
                          (event/send-message! behaviour :on-mouse-over))))

            :else
            (do (let [[ga comp] entered]
                  (letc ga [behaviour :behaviour]
                        (event/send-message! behaviour :on-mouse-exit)
                        (aset comp :entered false)))
                (let [[ga comp] rayed]
                  (letc ga [behaviour :behaviour]
                        (event/send-message! behaviour :on-mouse-enter)
                        (aset comp :entered true))))))))


(def-render-system debug-render! :box-collider
  [ga comp]
  (let [[tx ty bw bh] (get-boundary ga comp)]
    (-> ctx
        (js.canvas/save)
        (js.canvas/fill-style :#00FFFF)
        (js.canvas/stroke-rect {:x tx :y ty :w bw :h bh})
        (js.canvas/font-style "15pt Arial")
        (js.canvas/text {:x tx :y ty :text (str (aget comp :z)
                                                (aget comp :entered))})
        (js.canvas/restore))))
