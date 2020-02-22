(defproject testdoc "1.0.0"
  :description "Yet another doctest implementation in Clojure"
  :url "https://github.com/liquidz/testdoc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :profiles
  {:1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
   :1.10 {:dependencies [[org.clojure/clojure "1.10.0"]]}
   :1.10.1 {:dependencies [[org.clojure/clojure "1.10.1"]]}
   :test {:dependencies [[esac "0.1.0-SNAPSHOT"]]}
   :dev [:test {:dependencies [[org.clojure/clojure "1.10.1"]]}]}

  :aliases
  {"test-all" ["with-profile" "test,1.9:test,1.10:test,1.10.1" "test"]})
