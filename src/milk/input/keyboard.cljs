(ns milk.input.keyboard)


;; TODO(jdj) 키맵 완성 :: 현재 사용하는 키만 등록해놓음
(def ^:private keycode-map* {70 "f"
                             67 "c"
                             82 "r"
                             69 "e"
                             80 "p"})

(defn- keycode->key [keycode]
  (get keycode-map* keycode))

;; ========
;; privates.

(def ^:private downs* (atom #{}))
(def ^:private ups* (atom #{}))


(defn- get-keyboard-info []
  {:downs downs*
   :ups ups*})


(defn- on-keyboardup [e]
  (println "keyup " (keycode->key (.-keyCode e)))
  (swap! downs* disj (keycode->key (.-keyCode e)))
  (swap! ups* conj (keycode->key (.-keyCode e))))


(defn- on-keyboarddown [e]
  (swap! downs* conj (keycode->key (.-keyCode e))))


(defn ^:milk update! [events]

  ;; 키보드 정보 초기화
  (reset! downs* #{})
  (reset! ups* #{})

  ;; 이번트 처리
  (doseq [evt events]
    (condp = (:info evt)
      :up (on-keyboardup (:val evt))
      :down (on-keyboarddown (:val evt))
      nil)))


;; ========
;; publics.

(defn get-keyups
  [] @ups*)


(defn get-keydowns
  [] @downs*)


(defn is-keyup?
  [key] (contains? @ups* key))


(defn is-keydown?
  [key] (contains? @downs* key))
