(ns testdoc.core
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [clojure.walk :as walk]))

(defn- fn?'
  [x]
  (fn? (cond-> x (var? x) deref)))

(defn- form-line?
  [s]
  (str/starts-with? s "=>"))

(defn- join-forms
  [lines]
  (:result
   (reduce (fn [{:keys [tmp result] :as m} line]
             (cond
               (form-line? line)
               (assoc m :tmp (str/trim (str tmp "\n" (str/trim (subs line 2)))))

               (seq tmp)
               (assoc m :tmp "" :result (conj result tmp line))

               :else m))
           {:tmp "" :result []} lines)))

(defn- parse-doc
  [doc]
  (-> (str/trim doc)
      (str/split #"[\r\n]+")
      (->> (map str/trim)
           (remove str/blank?)
           (drop-while (complement form-line?))
           join-forms
           (map (comp read-string str/trim))
           (partition 2))))

(defn- replace-publics
  [x publics]
  (cond
    (symbol? x) (get publics x x)
    (sequential? x) (walk/postwalk-replace publics x)
    :else x))

(defn- get-message
  [form original-expected is-expected-fn?]
  (if is-expected-fn?
    (str "(true? (" original-expected " " form "))")
    (str "(= " form " " original-expected ")")))

(defn testdoc*
  [msg doc publics]
  (let [tests (parse-doc doc)
        last-actual (atom nil)]
    (reduce (fn [result [form original-expected]]
              (let [publics (assoc publics '*1 @last-actual)
                    actual (-> form (replace-publics publics) eval)
                    _ (reset! last-actual actual)
                    expected (-> original-expected (replace-publics publics) eval)
                    is-expected-fn? (fn?' expected)
                    pass? (if is-expected-fn?
                            (expected actual)
                            (= actual expected))]
                (conj result
                      {:type (if pass? :pass :fail)
                       :message (or msg (get-message form original-expected is-expected-fn?))
                       :expected original-expected
                       :actual actual})))
            [] tests)))

(defn testdoc [msg x]
  (cond
    (var? x)
    (let [{ns' :ns doc :doc} (meta x)
          publics (ns-publics ns')]
      (testdoc* msg doc publics))

    (string? x)
    (testdoc* msg x {})

    :else
    [{:type :fail
      :message (format "Unsupported document: %s" x)}]))

(defmethod t/assert-expr 'testdoc
  [msg [_ form]]
  `(doseq [result# (testdoc ~msg ~form)]
     (t/do-report result#)))

