(ns milk.macro)


(defmacro def-component [name params & body]
  (let [pairs (vec (for [[k v] (partition-all 2 body)]
                     [k v]))]
    `(defn ~name ~params
       (let [c# (gatom/make-component ~(keyword name))]
         (doseq [[k# v#] ~pairs]
           (aset c# k# v#))
         c#))))


(defmacro def-behaviour [name params lex & body]
  (let [pairs (vec (for [[k v] (partition-all 2 body)]
                     [k v]))]
    `(defn ~name ~params
       (let [~'self (gatom/make-component :behaviour)]
         (let ~lex
           (aset ~'self "behaviour" ~(keyword name))
           (aset ~'self  :order 0)
           (doseq [[k# v#] ~pairs]
             (aset ~'self k# v#))
           ~'self)))))


(defmacro def-prefab [name params & components]
  `(defn ~name ~params
     (let [ga# (gatom/make-gatom ~(keyword name))]
       (doseq [c# ~(vec components)]
         (-> ga# (aget "components") (.push c#)))
       ga#)))


(defmacro def-system [name component-keyword [ga comp] & body]
  `(defn ~name []
     (let [~'dt (time/delta-time)]
       (doseq [[~ga ~comp] (gatom/all-ga ~component-keyword)]
         ~@body))))


(defmacro def-render-system [name component-keyword [ga comp] & body]
  `(defn ~name []
     (let [~'ctx (screen.canvas/get-ctx)]
       (doseq [[~ga ~comp] (gatom/all-ga ~component-keyword)]
         ~@body))))


(defmacro letc [ga pairs & body]
  (let [pairs (vec (apply concat
                          (for [[k v] (partition-all 2 pairs)]
                            [k `(gatom/get-component ~ga ~v)])))]
    `(let ~pairs ~@body)))
