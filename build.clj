(ns build
  (:require
   [clojure.tools.build.api :as b]
   [clojure.xml :as xml]
   [deps-deploy.deps-deploy :as deploy]))

(def ^:private basis (b/create-basis {:project "deps.edn"}))
(def ^:private class-dir "target/classes")
(def ^:private jar-file "target/testdoc.jar")
(def ^:private lib 'com.github.liquidz/testdoc)
(def ^:private pom-file "./pom.xml")

(defn- get-current-version
  [pom-file-path]
  (->> (xml/parse pom-file-path)
       (xml-seq)
       (some #(and (= :version (:tag %)) %))
       (:content)
       (first)))

(defn pom
  [arg]
  (let [version (or (:version arg) (get-current-version pom-file))]
    (b/write-pom {:basis basis
                  :class-dir class-dir
                  :lib lib
                  :version version
                  :src-dirs ["src"]})
    (b/copy-file {:src (b/pom-path {:lib lib :class-dir class-dir})
                  :target pom-file})))

(defn jar
  [arg]
  (pom arg)
  (b/copy-dir {:src-dirs (:paths basis)
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn install
  [arg]
  (jar arg)
  (deploy/deploy {:artifact jar-file
                  :installer :local}))

(defn deploy
  [arg]
  (assert (and (System/getenv "CLOJARS_USERNAME")
               (System/getenv "CLOJARS_PASSWORD")))
  (jar arg)
  (deploy/deploy {:artifact jar-file
                  :installer :remote}))
