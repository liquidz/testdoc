(ns build
  (:require
   [clojure.string :as str]
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

(defn- get-next-version
  [current-version]
  (if-let [idx (str/last-index-of current-version ".")]
    (str (subs current-version 0 (inc idx))
         (b/git-count-revs nil))
    (throw (ex-info "Unexpected current-version format" {:current-version current-version}))))

(defn pom
  [_]
  (let [version (-> pom-file
                    (get-current-version)
                    (get-next-version))]
    (b/write-pom {:basis basis
                  :class-dir class-dir
                  :lib lib
                  :version version
                  :src-dirs ["src"]}))
  (b/copy-file {:src (b/pom-path {:lib lib :class-dir class-dir})
                :target pom-file}))

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