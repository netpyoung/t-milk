(ns milk.component.box-collider
  (:require-macros [milk.macro :refer [def-component]])
  (:require [milk.gatom :as gatom]))


(def-component box-collider [& {:keys [w h z] :or {}}]
  :w w
  :h h
  :z (or z 0)
  :entered false)
