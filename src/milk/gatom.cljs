(ns milk.gatom)


(def ^:private compo-manager*
  (js-obj "_cur_cid" -1
          "dic-id-component" (js-obj)))

(def ^:private gatom-manager*
  (js-obj "_cur_gid" -1
          "dic-id-gatom" (js-obj)))


(defn- gen-next-cid!
  "generate next component id"
  []
  (let [next-id (inc (aget compo-manager* "_cur_cid"))]
    (-> compo-manager* (aset "_cur_cid" next-id))
    next-id))


(defn- gen-next-gid!
  "generate next gatom id"
  []
  (let [next-id (inc (aget gatom-manager* "_cur_gid"))]
    (-> gatom-manager* (aset "_cur_gid" next-id))
    next-id))


(defn ^:milk make-component
  "make component"
  [name]
  (let [id (gen-next-cid!)
        co (js-obj "name" name
                   "type" :component
                   "_id" id
                   "enabled" true)]
    (-> compo-manager*
        (aget "dic-id-component")
        (aset id co))
    co))


(defn ^:milk make-gatom
  "make gatom"
  [name]
  (let [id (gen-next-gid!)
        ga (js-obj "name" name
                   "type" :gatom
                   "_id" id
                   "components" (array)
                   "reserved" false
                   "active" false
                   "live?" true)]
    (-> gatom-manager*
        (aget "dic-id-gatom")
        (aset id ga))
    ga))



(defn ^:milk activate!
  "activate gatom"
  [ga]
  (-> ga (aset "active" true)))


(defn ^:milk unregister!
  "unregister gatom from world"
  [ga]
  (when ga

    ;; clear gatom's components.
    (let [components (-> ga (aget "components"))
          dic-id-component (-> compo-manager* (aget "dic-id-component"))]

      (doseq [c components]

        (-> dic-id-component
            (js-delete (aget c "_id")))

        (doseq [m (.keys js/Object c)]
          (js-delete c m))))


    ;; clear gatom itselef.
    (-> gatom-manager*
        (aget "dic-id-gatom")
        (js-delete (aget ga "_id")))

    (doseq [m (.keys js/Object ga)]
      (js-delete ga m))

    (aset ga "live?" false)))



(defn ^:milk get-components
  "get components from gatom by component-name"
  [ga c-name]
  (when ga
    (filter (fn [comp] (= (.-name comp) c-name))
            (-> ga (aget "components")))))


(defn ^:milk get-component
  "get component from gatom"
  [ga c-name]
  (-> ga
      (get-components c-name)
      first))


(defn- get-behaviour
  "get behaviour from gatom by behaviour's name"
  [ga b-name]

  (when-let [comps (-> ga (get-components :behaviour))]
    (first (filter (fn [comp] (= (aget comp "behaviour") b-name))
                   comps))))


(defn ^:milk del-c!
  "delete component from gatom"
  [ga c]

  (let [components (-> ga (aget "components"))]
    (-> ga (aset "components" (clj->js (remove #{c} components)))))

  (let [dic-id-component (-> compo-manager* (aget "dic-id-component"))]
    (-> dic-id-component (js-delete (aget c "_id")))))


(defn ^:milk ^:debug get-registered-gatoms
  "get registered gatoms"
  []
  (let [dic (-> gatom-manager* (aget "dic-id-gatom"))]
    (.map (.keys js/Object dic) #(aget dic %))))


(defn ^:milk clear!
  "unregister all gatom"
  []

  (doseq [ga (get-registered-gatoms)]
    (when-not (.-reserved ga)
      (unregister! ga))))


(defn ^:milk all-ga
  "get all activate gatom. order by :order"
  [component-keyword]

  (sort (fn [[_ x] [_ y]]
          (< (aget x :order) (aget y :order)))
        (mapcat identity
                (for [ga (->> (get-registered-gatoms) (filter (fn [ga]
                                                                (= true (aget ga "active")))))]
                  (for [comp (get-components ga component-keyword)]
                    [ga comp])))))


(defn- get-gatom-by-name
  [gatom-name]

  (->> (get-registered-gatoms)
       (filter #(= (.-name %) gatom-name))
       (first)))


(defn- get-behaviour-id
  [ga behaviour-keyword]
  (-> ga
      (get-behaviour behaviour-keyword)
      .-_id))


;; ==========
;; publics.

(defn add-c!
  "add component to gatom"
  [ga c]
  (when (aget ga "live?")
    (-> ga
        (aget "components")
        (.push c))
    ga))


(def get-b get-behaviour)

(defn get-bid
  "get behaviour-id from gatom"
  [gatom behaviour-keyword]

  (if (string? gatom)
    (-> gatom
        get-gatom-by-name
        (get-behaviour-id behaviour-keyword))

    (-> gatom
        (get-behaviour-id behaviour-keyword))))


(defn call
  "call method from obj's field"
  [obj field & args]

  (-> obj
      (aget field)
      (apply args)))


(defn get-component-ids
  "get component's ids from gatom"
  [ga]

  (map (fn [c] [c (aget c "_id")])
       (-> ga (aget "components"))))


(defn get-component-by-id
  "get component by component's id"
  [component-id]

  (-> compo-manager*
      (aget "dic-id-component")
      (aget component-id)))


(def get-cs get-components)
(def get-c  get-component)
