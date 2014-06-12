(ns milk.input
  (:require [jayq.core]
            [milk.event :as event]
            [milk.input.mouse :as input.mouse]
            [milk.input.keyboard :as input.keyboard]))


(defn ^:mlik init! [element-id]

  (-> (jayq.core/$ element-id)
      (jayq.core/bind :mousemove #(event/mouse-event! :move %))
      (jayq.core/bind :mousedown #(event/mouse-event! :down %))
      (jayq.core/bind :mouseup   #(event/mouse-event! :up   %))
      (jayq.core/bind :keyup     #(event/keyboard-event! :up   %))
      (jayq.core/bind :keydown   #(event/keyboard-event! :down %))
      ))


(defn ^:milk update! [mouse-events keyboard-events]
  (input.mouse/update! mouse-events)
  (input.keyboard/update! keyboard-events))
