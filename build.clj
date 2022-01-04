(ns build
  (:require
   [clojure.java.shell :as sh]
   [clojure.tools.build.api :as b]
   [clojure.xml :as xml]
   [deps-deploy.deps-deploy :as deploy]
   [semver.core :as semver]))

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

(defn- get-next-version
  [current-version]
  (semver/transform semver/increment-patch current-version))

(defn pom
  [arg]
  (let [lib' (or (:lib arg) lib)
        version (or (:version arg) (get-current-version pom-file))]
    (b/write-pom {:basis basis
                  :class-dir class-dir
                  :lib lib'
                  :version version
                  :src-dirs ["src"]})
    (b/copy-file {:src (b/pom-path {:lib lib' :class-dir class-dir})
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

(defn release
  [{:as arg :keys [main-branch]}]
  (let [release-version (get-current-version pom-file)
        next-dev-version (get-next-version release-version)
        arg (assoc arg :version release-version)
        main-branch (or main-branch "main")]
    (println "Start to release v" release-version)
    (pom {:version release-version})
    (sh/sh "git" "commit" "-a" "-m" (str "Release v" release-version " [skip ci]"))
    (sh/sh "git" "push" "origin" main-branch)
    (sh/sh "git" "tag" "-a" release-version)
    (sh/sh "git" "push" "--tags" "origin")
    (deploy arg)

    (pom {:version next-dev-version})
    (sh/sh "git" "commit" "-a" "-m" (str "Prepare for next development iteration [skip ci]"))
    (sh/sh "git" "push" "origin" main-branch)))
