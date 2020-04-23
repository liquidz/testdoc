(ns bb-test-runner
  (:require
   [clojure.test :as t]
   [testdoc.core-test]))

(let [{:keys [:fail :error]} (t/run-tests 'testdoc.core-test)]
  (System/exit (+ fail error)))
