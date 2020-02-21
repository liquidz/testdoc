# testdoc
[![GitHub Actions for test workflow](https://github.com/liquidz/testdoc/workflows/test/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Atest)
[![GitHub Actions for lint workflow](https://github.com/liquidz/testdoc/workflows/lint/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Alint)
[![Dependencies Status](https://versions.deps.co/liquidz/testdoc/status.svg)](https://versions.deps.co/liquidz/testdoc)
[![Clojars Project](https://img.shields.io/clojars/v/testdoc.svg)](https://clojars.org/testdoc)

Yet another doctest implementation in Clojure

## Usage

testdoc extends `clojure.test/is` macro.

```clojure
(ns foo-test
  (require [clojure.test :as t]
           testdoc.core))

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
```

## Other works
* [drojas/doctest](https://github.com/drojas/doctest)
* [Kobold/clj-doc-test](https://github.com/Kobold/clj-doc-test/)

## License

Copyright © 2018-2020 [Masashi Iizuka](https://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
