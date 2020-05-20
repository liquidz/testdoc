(ns testdoc.style.code-first-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [testdoc.style.code-first :as sut]))

(defn- lines
  [ls]
  (str/join "\n" ls))

(t/deftest parse-doc-test
  (t/are [expected in] (= expected (#'sut/parse-doc (lines in)))
    '[[a b]],       ["a" ";; => b"]
    '[[(a b) c]],   ["(a" "b)" ";; => c"]
    '[[(a b) c]],   ["head" "(a" "b)" ";; => c"]
    '[[a b] [c d]], ["a" ";; => b" "c" ";; => d"]
    '[],            ["a"]
    '[[a b]],       ["a" ";; => b" "c"]
    '[[a b]],       ["a" ";; => b" ";; => c"]))
