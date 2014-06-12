(ns milk.scene)


(def ^:private scene-info* (atom nil))


(defn ^:milk init! [scene-info]
  (reset! scene-info* scene-info))


(defn ^:milk load-scene
  ([scene-k]
     (let [load-scene-fn (scene-k @scene-info*)]
       (load-scene-fn)))
  ([scene-k args]
     (let [load-scene-fn (scene-k @scene-info*)]
       (apply load-scene-fn args))))
