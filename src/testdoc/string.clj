(ns testdoc.string
  (:refer-clojure :exclude [replace re-seq seq subs])
  (:require
   [clojure.string :as str]))

;; NOTE: Babashka does not support `defrecord` yet
; (defrecord MetaString [string]
;   Object
;   (toString [_] string))

(defn new-string
  ([string] (new-string string {}))
  ([string metadata]
   ;; NOTE: Babashka adds `:line` metadata automatically
   (with-meta {:string string} metadata)))

(defn to-str
  [ms]
  (:string ms))

(defn trim
  [ms]
  (new-string (str/trim (:string ms)) (meta ms)))

(defn split-lines
  [ms]
  (let [m (meta ms)]
    (->> (str/split-lines (:string ms))
         ;; NOTE: Babashka adds `:line` metadata automatically
         (map-indexed (fn [i s] (new-string s (assoc m ::line (inc i))))))))

(defn blank?
  [ms]
  (str/blank? (:string ms)))

(defn replace
  [ms match replacement]
  (new-string (str/replace (:string ms) match replacement)
              (meta ms)))

(defn seq
  [ms]
  (when-let [res (clojure.core/seq (:string ms))]
    (with-meta res (meta ms))))

(defn re-seq
  [re ms]
  (when-let [res (clojure.core/re-seq re (:string ms))]
    (with-meta res (meta ms))))

(defn join
  [separator ms-coll]
  (new-string (str/join separator (map :string ms-coll))
              ;; Earlier metadata is the priority
              (apply merge (reverse (map meta ms-coll)))))

(defn starts-with?
  [ms substr]
  (str/starts-with? (:string ms) substr))

(defn subs
  ([ms start]
   (new-string (clojure.core/subs (:string ms) start)
               (meta ms)))
  ([ms start end]
   (new-string (clojure.core/subs (:string ms) start end)
               (meta ms))))
