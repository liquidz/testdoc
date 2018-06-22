(ns testdoc.core-test
  (:require [clojure.test :as t]
            [testdoc.core :as sut]
            [fudje.sweet :as fj]))

(t/deftest join-forms-test
  (t/are [x y] (= x (#'sut/join-forms y))
    ["a" "b"]         ["=> a" "b"]
    ["a b" "c"]       ["=> a" "=> b" "c"]
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
  10"
  [])

(defn- partial-success-test-func
  "foo bar

  => (+ 1 2 3)
  6
  => (+ 1 2 3 4)
  11"
  [])

(t/deftest testdoc-test
  (t/is
   (compatible
    (sut/testdoc nil #'success-test-func)
    (fj/contains [{:type :pass :expected 6 :actual 6}
                  {:type :pass :expected 10 :actual 10}]
                 :in-any-order)))

  (t/is
   (compatible
    (sut/testdoc nil #'partial-success-test-func)
    (fj/contains [{:type :pass :expected 6 :actual 6}
                  {:type :fail :expected 11 :actual 10}]))))

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
