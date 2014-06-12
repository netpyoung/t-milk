(ns milk.component.transform
  (:require-macros [milk.macro :refer [def-component]])
  (:require [milk.gatom :as gatom]))


(def-component transform [& {:keys [pos] :or {pos [0 0]}}]
  :pos pos)
