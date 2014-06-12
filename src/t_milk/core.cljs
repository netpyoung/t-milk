(ns t-milk.core
  (:require
   [milk.gameloop]
   [t-milk.scene :as scene]
   ))


(def ^:private running?* (atom false))


(defn ^:export main []
  (when-not @running?*
    (reset! running?* true)

    (milk.gameloop/init! {:default scene/load})
    (milk.gameloop/start!)
    ))
