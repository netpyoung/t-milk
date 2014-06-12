(ns milk.event
  (:require [milk.event-manager :as event-manager]
            [milk.gatom :as gatom]
            [milk.component.transform :refer [transform]] ; [deprecated]
            [cljs.core.async :refer [<! timeout] :as async]
            [goog.userAgent :as user-agent])
  (:require-macros [cljs.core.async.macros :refer [go]]))


;; =========
;; milk.
(defn ^:milk mouse-event!
  "register mouse event"
  [event-type e]
  (if user-agent/IE
    ;; Internet Explorer 11에서 mouse events 발생시, canvas animation의
    ;; fps를 현저히 떨어뜨리는 문제를 막기 위한 코드
    (go (<! (timeout 10))
      (event-manager/register-event! {:type :mouse
                                      :info event-type
                                      :val e}))
    (event-manager/register-event! {:type :mouse
                                      :info event-type
                                      :val e})))


(defn ^:milk keyboard-event!
  "register keyboard event"
  [event-type e]
  (event-manager/register-event! {:type :keyboard
                                  :info event-type
                                  :val e}))

;; =========
;; publics.
(defn add-gatom!
  "add gatom to world"
  [name comps]
  (let [ga (gatom/make-gatom name)]
    (doseq [comp comps]
      (gatom/add-c! ga comp))
    (event-manager/register-event! {:type :gatom
                                    :info :register
                                    :val ga})
    ga))


(defn del-gatom!
  "del gatom from world"
  [ga]
  (event-manager/register-event! {:type :gatom
                                  :info :destroy
                                  :val ga}))


(defn new-gatom
  "[DEPRECATED] add gatom to world"
  [name comps & {:keys [pos] :or {pos [0 0]}}]

  (let [ga (gatom/make-gatom name)]

    (gatom/add-c! ga (transform :pos pos))

    (doseq [comp comps]
      (gatom/add-c! ga comp))

    (event-manager/register-event! {:type :gatom
                                    :info :register
                                    :val ga})
    ga))


(defn del-gatom
  "[DEPRECATED] del gatom from world"
  [ga]
  (del-gatom! ga))


(defn add-component!
  "add component to gatom"
  [ga component]
  (when ga
    (event-manager/register-event! {:type :component
                                    :info :add
                                    :val [ga component]})))


(defn destroy-component!
  "destroy component from gatom"
  [ga component]
  (event-manager/register-event! {:type :component
                                  :info :destroy
                                  :val [ga component]}))


(defn send-message!
  "send message (k) to component with (args)"
  [component k & args]
  (event-manager/register-event! {:type :component
                                  :info :send-message
                                  :val [component k args]}))


(defn load-scene!
  "load next level"
  [scene-k & args]

  ;; TODO(kep) gameloop란 이름이 맘에 안듬.
  (event-manager/register-event! {:type :gameloop
                                  :info :load-level
                                  :val [scene-k args]}))
