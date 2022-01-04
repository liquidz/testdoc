# testdoc
[![GitHub Actions for test workflow](https://github.com/liquidz/testdoc/workflows/test/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Atest)
[![GitHub Actions for babashka test workflow](https://github.com/liquidz/testdoc/workflows/babashka/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Ababashka)
[![GitHub Actions for lint workflow](https://github.com/liquidz/testdoc/workflows/lint/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Alint)
[![Clojars Project](https://img.shields.io/clojars/v/com.github.liquidz/testdoc.svg)](https://clojars.org/com.github.liquidz/testdoc)

Yet another doctest implementation in Clojure

## Usage

testdoc extends `clojure.test/is` macro.

### REPL style

```clojure
=> (require '[clojure.test :as t]
=>          'testdoc.core)
nil

=> (defn myplus
=>   "Add a and b
=>
=>   => (myplus 1 2)
=>   3
=>   => (myplus 2
=>   =>         3)
=>   5"
=>   [a b]
=>   (+ a b))
var?

=> (t/deftest myplus-test
=>   (t/is (testdoc #'myplus)))
var?

=> (t/test-var *1)
nil

;; Other examples
=> (hash-map :multiple "lines")
{:multiple
 "lines"}
```

### Code-first style

```clojure
(defn mymulti
  "Multiply a and b

  (mymulti 1 2)
  ;; => 2
  (mymulti 2
           3)
  ;; => 6"
  [a b]
  (* a b))
;; => var?

(t/deftest mymulti-test
  (t/is (testdoc #'mymulti)))
;; => var?

(t/test-var *1)
;; => nil

;; Other examples
(hash-map :multiple "lines")
;; => {:multiple
;; =>  "lines"}
```

### Testing external documents

This document is tested by this library, of course!

```clojure
(require '[clojure.java.io :as io])
;; => nil

(t/deftest external-document-test
  (t/is (testdoc (slurp (io/file "test/resources/README.adoc")))))
;; => var?

(t/test-var *1)
;; => nil
```

## Test error

testdoc will add a line number information to a error message text.

For example, you have a test code like below:
```clojure
(t/deftest error-test
  (t/is (testdoc "=> (unresolved-symbol)
                  :failure")))
```

Then, `lein test` will show you errors like below:

```
(= (unresolved-symbol) :failure), [line: 1]
```

## Other works
* [drojas/doctest](https://github.com/drojas/doctest)
* [Kobold/clj-doc-test](https://github.com/Kobold/clj-doc-test/)

## License

Copyright © 2018-2022 [Masashi Iizuka](https://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
