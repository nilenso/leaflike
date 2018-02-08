(ns leaflike.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

(def ^:private config
  (delay
   (let [env-type (or (System/getenv "LEAFLIKE_ENV")
                      "dev")
         config-filename (str "config/config.edn." env-type)]
     (log/info (str "Loading config file " config-filename))
     (edn/read-string (slurp (io/resource config-filename))))))

(defn db-spec
  []
  (:db-spec @config))

(defn server-spec
  []
  (:server-spec @config))
