(ns milk.input.mouse
  (:require [milk.screen.canvas]))


;; NOTE(kep) 0, 1, 2번 마우스 버튼에 대해, 단일 클릭만 고려함.

(defn- get-last-event [events info-name]
  ;; TODO(kep) 이 함수 맘에 안듬.
  (last (filter #(= (:info %) info-name) events)))


(defn- get-offset
  "mouse offset을 구함.
   ref: http://www.jacklmoore.com/notes/mouse-position/ "
  [e]

  (if (or (.-offsetX e) (.-offsetY e))

    [(.-offsetX e) (.-offsetY e)]

    (let [clientX (.-clientX e)
          clientY (.-clientY e)
          rect (-> e .-target .getBoundingClientRect)]

      [(- clientX (.-left rect)) (- clientY (.-top rect))])))


(def ^:private pos* (atom [0 0]))
(def ^:private down?* (atom false))
(def ^:private button* (atom -1))


(defn- get-mouse-info []
  {:pos @pos*
   :down? @down?*
   :button @button*})


(defn- on-mousemove [e]
  (let [[ex ey] (get-offset e)
        [rw rh] (milk.screen.canvas/get-r)]
    (reset! pos* [(/ ex rw) (/ ey rh)])
    )

  (let [which (.-which e)]
    (if (or (= which 1) (= which 2) (= which 3))
      (do (reset! button* (.-button e))
          (reset! down?* true))
      (reset! down?* false))))


(defn- on-mouseup [e]
  (reset! button* (.-button e))
  (reset! down?* false))


(defn- on-mousedown [e]
  (reset! button* (.-button e))
  (reset! down?* true))


;; cur
(def ^:private current-mouse-info*
  (atom {:pos [0 0]
         :down? false
         :button 0}))


(def ^:private current-mouse-status*
  (atom {:upped? false
         :downed? false
         :movement [0 0]}))


(defn- next-mouse-status [old new]
  (let [[nx ny] (:pos new)
        [ox oy] (:pos old)
        movement [(- nx ox) (- ny oy)]]
    (cond
     (= (:down? old) (:down? new))
     {:downed? false :upped? false :movement movement}

     (:down? new)
     {:downed? true :upped? false :movement movement}

     :else
     {:downed? false :upped? true :movement movement})))


(add-watch current-mouse-info*
           :changed (fn [k a old new]
                      (reset! current-mouse-status* (next-mouse-status old new))))


(defn- update-current-mouse-info! []
  (reset! current-mouse-info* (get-mouse-info)))



(defn ^:milk update! [events]
  (when-let [evt (get-last-event events :up)]
    (on-mouseup (:val evt)))

  (when-let [evt (get-last-event events :move)]
    (on-mousemove (:val evt)))

  (when-let [evt (get-last-event events :down)]
    (on-mousedown (:val evt)))

  (update-current-mouse-info!))


;; ========
;; publics.
(defn mouse-pos []
  (:pos @current-mouse-info*))


(defn mouse-movement []
  (:movement @current-mouse-status*))


(defn get-mouse-button
  "
   ref: http://www.w3schools.com/jsref/event_button.asp

   #button
   - 0  Specifies the left mouse-button
   - 1  Specifies the middle mouse-button
   - 2  Specifies the right mouse-button

   #which
   - :up
   - :down"

  ([button]
     ;; held down.
     (and (= button (:button @current-mouse-info*))
          (:down? @current-mouse-info*)))

  ([button which]
     ;; during frame.
     (if (= button (:button @current-mouse-info*))
       (case which
         :up   (:upped? @current-mouse-status*)
         :down (:downed? @current-mouse-status*)
         false)
       false)))
