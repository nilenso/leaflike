(ns leaflike.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private config (edn/read-string (slurp (io/resource "config.edn"))))

(defn test-db-spec
  []
  (:test-db-spec config))

(defn db-spec
  []
  (:db-spec config))

(defn server-spec
  []
  :server-spec config)
