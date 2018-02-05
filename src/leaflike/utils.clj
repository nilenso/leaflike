(ns leaflike.utils
  (:require [clj-time.coerce]
            [clj-time.core]))

(def email-pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(def alpha-num-pattern #"^[a-zA-Z0-9]+$")

(defn get-timestamp
  []
  (clj-time.coerce/to-sql-time (clj-time.core/now)))

(defn required
  [value]
  (and (string? value)
       (not-empty value)))
