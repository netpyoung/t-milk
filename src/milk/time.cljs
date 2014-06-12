(ns milk.time)


(def ^:private current-time* (atom 0))
(def ^:private delta-time* (atom 0))


(defn ^:milk init! []
  (reset! current-time* (.now js/Date)))


(defn ^:milk update! []
  (let [new-t (.now js/Date)
        old-t @current-time*]
    (reset! delta-time* (- new-t old-t))
    (reset! current-time* new-t)))


;; ========
;; publics.

(defn current-time [] @current-time*)
(defn delta-time   [] @delta-time*)
