(ns testdoc.core-test
  (:require
   [clojure.test :as t]
   [esac.core :as esac]
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
   (esac/match?
    (sut/testdoc nil #'success-test-func)
    ^:in-any-order [{:type :pass :expected _ :actual 6}
                    {:type :pass :expected 10 :actual 10}]))

  (t/is
   (esac/match?
    (sut/testdoc nil #'partial-success-test-func)
    [{:type :pass :expected 6 :actual 6}
     {:type :fail :expected 11 :actual 10}])))

(t/deftest testdoc-unsupported-test
  (t/is
   (esac/match?
    (sut/testdoc nil 123)
    [{:type :fail :message #"^Unsupported document:"}])))

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
