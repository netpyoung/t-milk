(ns t-milk.scene
  "slotmachien main scene."
  (:require
   [milk.event :as event :refer [new-gatom]]

   ;; debug.
   [t-milk.behaviour.fps :refer [fps]]
   ))


;; main.
(defn load []
  (def a (event/new-gatom "fps" [(fps)] :pos [100 50]))
  )
