(ns testdoc.style.repl
  (:require
   [clojure.string :as str]))

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
                 (assoc m :tmp (str/trim (str tmp "\n" (str/trim (subs line n)))))

                 (seq tmp)
                 (assoc m :tmp "" :result (conj result tmp line))

                 :else m))
             {:tmp "" :result []} lines))))

(defn parse-doc
  [doc]
  (-> (str/trim doc)
      (str/split #"[\r\n]+")
      (->> (map str/trim)
           (remove str/blank?)
           (drop-while (complement form-line?))
           join-forms
           (map (comp read-string str/trim))
           (partition 2))))
