(ns testdoc.style.code-first
  (:require
   [testdoc.string :as str]))

(def ^:private output-line-re #"^;+ ?=>")

(defn- output-line?
  [s]
  (some? (str/re-seq output-line-re s)))

(defn- join-forms
  [lines]
  (let [res (reduce
             (fn [{:keys [tmp result level] :as m} line]
               (cond
                 (and (or (empty? tmp)
                          (= 1 (count tmp)))
                      (output-line? line))
                 (let [expected (or (first tmp) (str/new-string "" {}))
                       expected (str/trim (str/join "\n" [(str/replace line output-line-re "")
                                                          expected]))]
                   (assoc m :tmp [expected] :level 0))

                 (seq tmp)
                 (let [next-level (+ level (str/calc-level line))
                       [expected & codes :as tmp] (conj tmp line)]
                   (if (= 0 next-level)
                     (assoc m :tmp [] :level 0 :result (conj result
                                                             (str/join "\n" (reverse codes))
                                                             expected))
                     (assoc m :tmp tmp :level next-level)))

                 :else m))
             {:tmp [] :result [] :level 0}
             (reverse lines))
        {:keys [level result]} res]
    (when (neg? level)
      (throw (ex-info "Unmatched parentheses: too many closing parentheses" {:lines lines})))
    (when (pos? level)
      (throw (ex-info "Unmatched parentheses: too few closing parentheses" {:lines lines})))
    result))

(defn parse-doc
  [doc]
  (-> (str/new-string doc)
      (str/split-lines)
      (->> (map str/trim)
           (remove str/blank?)
           (join-forms)
           (map #(let [x (-> % str/trim str/to-str read-string)]
                   (cond-> x (instance? clojure.lang.IObj x) (with-meta (meta %)))))
           (partition 2)
           (map #(with-meta % (meta (first %))))
           (reverse))))
