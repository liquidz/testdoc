(ns testdoc.style.repl-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [testdoc.style.repl :as sut]))

(defn- lines
  [ls]
  (str/join "\n" ls))

(t/deftest parse-doc-test
  (t/are [expected in] (= expected (#'sut/parse-doc (lines in)))
    '[[a b]],       ["=> a" "b"]
    '[[(a b) c]],   ["=> (a" "=> b)" "c"]
    '[[a b] [c d]], ["=> a" "b" "=> c" "d"]
    '[],            ["=> a"]
    '[[a b]],       ["=> a" "b" "=> c"]
    '[[a b]],       ["=> a" "b" "c"]))
