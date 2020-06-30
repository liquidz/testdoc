(ns testdoc.style.repl
  (:require
   [testdoc.string :as str]))

(def ^:private form-line-str "=>")

(defn- form-line?
  [s]
  (str/starts-with? s form-line-str))

(defn- join-forms
  [lines]
  (let [n (count form-line-str)]
    (:result
     (reduce (fn [{:keys [tmp result] :as m} line]
               (cond
                 (form-line? line)
                 (assoc m :tmp (str/trim (str/join "\n" [tmp (str/trim (str/subs line n))])))

                 (str/seq tmp)
                 (assoc m :tmp (str/new-string "") :result (conj result tmp line))

                 :else m))
             {:tmp (str/new-string "") :result []} lines))))

(defn parse-doc
  [doc]
  (-> (str/new-string doc)
      (str/split-lines)
      (->> (map str/trim)
           (remove str/blank?)
           (drop-while (complement form-line?))
           (join-forms)
           (map #(let [x (-> % str/trim str/to-str read-string)]
                   (cond-> x (instance? clojure.lang.IObj x) (with-meta (meta %)))))
           (partition 2)
           (map #(with-meta % (meta (first %)))))))
