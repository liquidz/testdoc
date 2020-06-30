(ns testdoc.style.code-first-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [testdoc.style.code-first :as sut]))

(defn- lines
  [ls]
  (str/join "\n" ls))

(t/deftest parse-doc-test
  (t/are [expected in] (= expected (sut/parse-doc (lines in)))
    '[[a b]],       ["a" ";; => b"]
    '[[(a b) c]],   ["(a" "b)" ";; => c"]
    '[[(a b) c]],   ["head" "(a" "b)" ";; => c"]
    '[[a b] [c d]], ["a" ";; => b" "c" ";; => d"]
    '[],            ["a"]
    '[[a b]],       ["a" ";; => b" "c"]
    '[[a b]],       ["a" ";; => b" ";; => c"]))

(t/deftest parse-doc-with-meta-test
  (let [ret (sut/parse-doc (lines ["" "a" ";; => 6" "c" ";; => :d"]))]
    (t/is (= '[[a 6] [c :d]] ret))
    (t/is (= {:line 2} (meta (first ret))))
    (t/is (= {:line 4} (meta (second ret))))))
