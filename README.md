# testdoc
[![GitHub Actions for test workflow](https://github.com/liquidz/testdoc/workflows/test/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Atest)
[![GitHub Actions for lint workflow](https://github.com/liquidz/testdoc/workflows/lint/badge.svg)](https://github.com/liquidz/testdoc/actions?query=workflow%3Alint)
[![Dependencies Status](https://versions.deps.co/liquidz/testdoc/status.svg)](https://versions.deps.co/liquidz/testdoc)
[![Clojars Project](https://img.shields.io/clojars/v/testdoc.svg)](https://clojars.org/testdoc)

Yet another doctest implementation in Clojure

## Usage

testdoc extends `clojure.test/is` macro.

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
=>   =>       3)
=>   5"
=>   [a b]
=>   (+ a b))
any?

=> (t/deftest myplus-test
=>   (t/is (testdoc #'myplus)))
any?

=> (t/test-var *1)
nil
```

### Testing external documents

```clojure
=> (require '[clojure.java.io :as io])
nil

=> (t/deftest external-document-test
=>   (t/is (testdoc (slurp (io/file "test/resources/README.adoc")))))
any?

=> (t/test-var *1)
nil
```

## Other works
* [drojas/doctest](https://github.com/drojas/doctest)
* [Kobold/clj-doc-test](https://github.com/Kobold/clj-doc-test/)

## License

Copyright © 2018-2020 [Masashi Iizuka](https://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
