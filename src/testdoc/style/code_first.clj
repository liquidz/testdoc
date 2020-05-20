(ns testdoc.style.code-first
  (:require
   [clojure.string :as str]))

(def ^:private output-line-re #"^;+ ?=>")

(defn- output-line?
  [s]
  (some? (re-seq output-line-re s)))

(defn- calc-level
  [s]
  (- (count (filter #{\(} s))
     (count (filter #{\)} s))))

(defn- join-forms
  [lines]
  (:result
   (reduce
    (fn [{:keys [tmp result level] :as m} line]
      (cond
        (and (or (empty? tmp)
                 (= 1 (count tmp)))
             (output-line? line))
        (let [expected (str/trim (str/replace line output-line-re ""))]
          (assoc m :tmp [expected] :level 0))

        (seq tmp)
        (let [next-level (+ level (calc-level line))
              [expected & codes :as tmp] (conj tmp line)]
          (if (= 0 next-level)
            (assoc m :tmp [] :level 0 :result (conj result
                                                    (str/join "\n" (reverse codes))
                                                    expected))
            (assoc m :tmp tmp :level next-level)))

        :else m))
    {:tmp [] :result [] :level 0}
    (reverse lines))))

(defn parse-doc
  [doc]
  (-> (str/trim doc)
      (str/split #"[\r\n]+")
      (->> (map str/trim)
           (remove str/blank?)
           join-forms
           (map (comp read-string str/trim))
           (partition 2)
           reverse)))
