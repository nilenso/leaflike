(ns leaflike.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.validator :refer [is-valid?]])
  (:import [java.util Date TimeZone]
           [java.text SimpleDateFormat]
           [java.sql Timestamp]))

(defn get-timestamp
  []
  (let [date (Date.)]
    (TimeZone/setDefault (TimeZone/getTimeZone "UTC"))
    (.format (SimpleDateFormat. "yyyy-mm-dd hh:mm:ss") date)
    (Timestamp. (.getTime date))))


(defn db-spec [] {:connection-uri "jdbc:postgresql://localhost:5432/leaflike"})

(defn add-created-at
  [body]
  (assoc body :created_at (get-timestamp)))

(defn create-bookmark
  [request]
  (let [body (-> request :body)
        bkm (add-created-at body)]
    (if (= (is-valid? body) true)
      (jdbc/insert! (db-spec) :bookmarks bkm)
      ; else
      {:error "Invalid Data"})))
