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
                 ;; code
                 (form-line? line)
                 (let [m (cond-> m
                           (= 2 (count tmp))
                           (assoc :tmp [] :result (conj result (first tmp) (second tmp))))
                       code (or (first (:tmp m)) (str/new-string ""))
                       code (str/trim (str/join "\n" [code (str/trim (str/subs line n))]))]
                   (assoc m :tmp [code]))

                 ;; expected
                 (and (str/seq line)
                      (seq tmp))
                 (let [[code & expected] (conj tmp line)]
                   (assoc m :tmp [code (str/join "\n" expected)]))

                 :else
                 (if (= 2 (count tmp))
                   (assoc m :tmp [] :result (conj result (first tmp) (second tmp)))
                   (assoc m :tmp []))))
             {:tmp [] :result []}
             lines))))

(defn parse-doc
  [doc]
  (-> (str/new-string doc)
      (str/split-lines)
      (concat [(str/new-string "")]) ; terminator
      (->> (map str/trim)
           (drop-while (complement form-line?))
           (join-forms)
           (map #(let [x (-> % str/trim str/to-str read-string)]
                   (cond-> x (instance? clojure.lang.IObj x) (with-meta (meta %)))))
           (partition 2)
           (map #(with-meta % (meta (first %)))))))
