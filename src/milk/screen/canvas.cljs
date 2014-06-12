(ns milk.screen.canvas
  (:require [jayq.core]
            [js.canvas.core :as js.canvas]))

(def ^:private canvas* (atom nil))
(def ^:private ctx* (atom nil))
(def ^:private width* (atom nil))
(def ^:private height* (atom nil))


(def ^:private view-w* (atom nil))
(def ^:private view-h* (atom nil))


(def ^:private r-w* (atom nil))
(def ^:private r-h* (atom nil))


(defn- get-context-info [element-id]
  (let [canvas (-> (jayq.core/$ element-id) (.get 0))]
    [(.-width canvas) (.-height canvas) canvas (-> canvas (js.canvas/get-context :2d))]))


(defn ^:milk set-view! [vw vh]
  ;; canvas {
  ;; width: 760px;
  ;; height: 570px;
  (let [canvas @canvas*
        width @width*
        height @height*]
    (-> canvas
        (aget "style")
        (aset "width" (str vw "px")))

    (-> canvas
        (aget "style")
        (aset "height" (str vh "px"))
        )

    (reset! view-w* vw)
    (reset! view-h* vh)

    (reset! r-w* (/ vw width))
    (reset! r-h* (/ vh height))
    ))

(defn ^:milk init! [element-id]
  (let [[w h canvas ctx] (get-context-info element-id)]

    (reset! canvas* canvas)
    (reset! ctx* ctx)
    (reset! width* w)
    (reset! height* h)
    (set-view! 1024 768);760 570)
    ))


(defn ^:milk save! []
  (-> @ctx*
      (js.canvas/save)))


(defn ^:milk clear! []
  (-> @ctx*
      (js.canvas/clear-rect {:x 0 :y 0 :w @width* :h @height*})))


(defn ^:milk restore! []
  (-> @ctx*
      (js.canvas/restore)))


(defn get-ctx []
  @ctx*)

(defn get-wh []
  [@width* @height*])

(defn get-r []
  [@r-w* @r-h*])
