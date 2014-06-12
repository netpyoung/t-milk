(ns milk.exception
  (:require [milk.event :as event]))


(defn err-handle [& {:keys [ga info err]}]
  (.log js/console (str "ga : " (if ga (aget ga "name")) " /n info : " info " /n err : " err))

  ;; 문제있는 컴포넌트를 가지고 있는 가톰을 폭파시킨다...
  ;; (if ga
  ;;   (event/del-gatom ga))
  )
