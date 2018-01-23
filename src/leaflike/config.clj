(ns leaflike.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private config (delay (edn/read-string (slurp (io/resource "config/config.edn")))))

(defn db-spec
  []
  (:db-spec @config))

(defn server-spec
  []
  (:server-spec @config))
