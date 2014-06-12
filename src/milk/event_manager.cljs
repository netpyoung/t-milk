(ns milk.event-manager)


(def ^:private frame-events* (atom []))
(def ^:private buffered-events* (atom []))


(defn ^:milk flush! []
  (reset! frame-events* @buffered-events*)
  (reset! buffered-events* []))


(defn ^:milk get-events
  ([]
     @frame-events*)
  ([type-name]
     (filter #(= (:type %) type-name) @frame-events*)))


(defn ^:milk register-event! [e]
  (swap! buffered-events* conj e))
