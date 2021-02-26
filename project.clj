(defproject testdoc "1.4.2-SNAPSHOT"
  :description "Yet another doctest implementation in Clojure"
  :url "https://github.com/liquidz/testdoc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :profiles
  {:1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
   :1.10 {:dependencies [[org.clojure/clojure "1.10.0"]]}
   :1.10.1 {:dependencies [[org.clojure/clojure "1.10.1"]]}
   :dev {:dependencies [[org.clojure/clojure "1.10.1"]]}
   :antq {:dependencies [[antq "RELEASE"]]}}

  :aliases
  {"test-all" ["with-profile" "1.9:1.10:1.10.1" "test"]})
