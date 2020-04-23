(ns testdoc.core-test
  (:require
   [clojure.java.io :as io]
   [clojure.test :as t]
   [testdoc.core :as sut]))

(t/deftest join-forms-test
  (t/are [x y] (= x (#'sut/join-forms y))
    ["a" "b"]         ["=> a" "b"]
    ["a\nb" "c"]       ["=> a" "=> b" "c"]
    ["a" "b" "c" "d"] ["=> a" "b" "=> c" "d"]
    []                ["=> a"]
    ["a" "b"]         ["=> a" "b" "=> c"]
    ["a" "b"]         ["=> a" "b" "c"]))

(defn- success-test-func
  "foo bar

  => (+ 1 2 3)
  6
  => (+ 1 2
  =>    3 4)
  10
  => (inc *1)
  11"
  [])

(defn- partial-success-test-func
  "foo bar

  => (+ 1 2 3)
  6
  => (+ 1 2 3 4)
  11"
  [])

(t/deftest testdoc-test
  (t/is (= #{{:type :pass :expected 6 :actual 6}
             {:type :pass :expected 10 :actual 10}
             {:type :pass :expected 11 :actual 11}}
           (->> (sut/testdoc nil #'success-test-func)
                (map #(select-keys % [:type :expected :actual]))
                set)))

  (t/is (= #{{:type :pass :expected 6 :actual 6}
             {:type :fail :expected 11 :actual 10}}
           (->> (sut/testdoc nil #'partial-success-test-func)
                (map #(select-keys % [:type :expected :actual]))
                set))))

(t/deftest testdoc-unsupported-test
  (let [[result :as results] (sut/testdoc nil 123)]
    (t/is (= 1 (count results)))
    (t/is (= :fail (:type result)))
    (t/is (re-seq #"^Unsupported document:" (:message result)))))

(defn plus
  "Add a and b

  => (plus 1 2)
  3
  => (plus 2
  =>       3)
  5"
  [a b]
  (+ a b))

(t/deftest plus-test
  (t/is (testdoc #'plus)))

(t/deftest plus-string-test
  (t/is (testdoc "=> (require '[testdoc.core-test :as ct])
                  nil
                  => (ct/plus 1 2)
                  3
                  => (ct/plus 2
                  =>          3)
                  5")))

(t/deftest README-test
  (t/is (testdoc (slurp (io/file "README.md")))))
