(ns leaflike.db
  (:require [clojure.java.jdbc :as jdbc]))

(defn db-spec [] {:connection-uri "jdbc:postgresql://localhost:5432/leaflike"})

(defn create-bookmark
  [data-map]
  (jdbc/insert! (db-spec) :bookmarks data-map))
