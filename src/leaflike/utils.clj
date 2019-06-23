(ns leaflike.utils
  (:require [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]))

(def email-pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(def alpha-num-pattern #"^[a-zA-Z0-9]+$")

(defn get-timestamp
  []
  (time-coerce/to-sql-time (time/now)))

(defn required
  [value]
  (and (string? value)
       (not-empty value)))

(defn debug [m]
  (println m)
  m)

(defn vals-from-list-of-id-maps
  [ms]
  (map :id (into [] ms)))

