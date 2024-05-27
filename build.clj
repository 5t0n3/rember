(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'rember)
(def version "0.1.0")
(def class-dir "target/classes")
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"})
  (println "Deleted target/ directory"))

(defn uberjar [_]
  (clean nil)
  (b/compile-clj {:basis @basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (println "code compiled, assembling into uberjar...")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis @basis
           :main 'rember.core})
  (println (format "wrote uberjar to %s" uber-file)))
