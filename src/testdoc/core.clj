(ns testdoc.core
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [clojure.walk :as walk]
   [testdoc.style.code-first :as style.code-first]
   [testdoc.style.repl :as style.repl]))

(defn- fn?'
  [x]
  (fn? (cond-> x (var? x) deref)))

(defn- parse-doc
  [doc]
  (concat (style.repl/parse-doc doc)
          (style.code-first/parse-doc doc)))

(defn- replace-interns
  [x interns]
  (cond
    (symbol? x) (get interns x x)
    (sequential? x) (walk/postwalk-replace interns x)
    :else x))

(defn- get-message
  [form original-expected is-expected-fn?]
  (cond
    is-expected-fn?  (str "(true? (" original-expected " " form "))")
    (nil? original-expected) (str "(= " form " nil)")
    :else (str "(= " form " " original-expected ")")))

(defn- try-eval
  [form]
  (try
    (eval form)
    (catch Throwable ex
      ex)))

(defn testdoc*
  [msg doc interns]
  (let [tests (parse-doc doc)
        last-actual (atom nil)]
    (reduce (fn [result [form original-expected :as test]]
              (let [interns (assoc interns '*1 @last-actual)
                    actual (-> form
                               (replace-interns interns)
                               (try-eval))
                    _ (reset! last-actual actual)
                    expected (-> original-expected
                                 (replace-interns interns)
                                 try-eval)
                    is-expected-fn? (fn?' expected)
                    pass? (if is-expected-fn?
                            (expected actual)
                            (= actual expected))
                    line-number (-> test meta :testdoc.string/line)]
                (conj result
                      {:type (if pass? :pass :fail)
                       :message (cond-> (or msg (get-message form original-expected is-expected-fn?))
                                  line-number (str ", [line: " line-number "]"))
                       :expected original-expected
                       :actual actual})))
            [] tests)))

(defn- ns-interns*
  [ns-sym]
  (reduce-kv
   (fn [accm k v]
     (assoc accm k
            (if (:private (meta v))
              ;; NOTE: Convert to be able to evaluate private symbols in `eval`
              `(var ~(symbol v))
              v)))
   {}
   (ns-interns ns-sym)))

(defn extract-document
  [x]
  (cond
    (var? x)
    (let [{ns' :ns doc :doc} (meta x)]
      [doc (ns-interns* ns')])

    (string? x)
    [x {}]

    :else
    nil))

(defn testdoc
  [msg x]
  (if-let [[doc interns] (extract-document x)]
    (if (str/blank? doc)
      [{:type :fail
        :message (format "No document: %s" x)}]
      (testdoc* msg doc interns))
    [{:type :fail
      :message (format "Unsupported document: %s" x)}]))

(defmethod t/assert-expr 'testdoc
  [msg [_ form]]
  `(doseq [result# (testdoc ~msg ~form)]
     (t/do-report result#)))

(defn debug
  "Print parsed codes and expected results

  ```
  => (debug \"=> (+ 1 2)\\n3\")
  nil

  => (debug \"(+ 1 2)\\n;; => 3\")
  nil
  ```"
  [x]
  (doseq [[x y] (some-> x extract-document first parse-doc)]
    (println "----")
    (println "CODE     >>" (pr-str x))
    (println "EXPECTED >>" (pr-str y))))
