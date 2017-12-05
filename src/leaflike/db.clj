(ns leaflike.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.validator :refer [is-valid-bookmark?]])
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
    (if (= (is-valid-bookmark? body) true)
      (jdbc/insert! (db-spec) :bookmarks bkm)
      ; else
      {:error "Invalid Data"})))

(defn list-all
  []
  (jdbc/query (db-spec) ["select * from bookmarks"]))


(defn list-by-id
  [params]
  (jdbc/query (db-spec) ["select * from bookmarks
                          where id = ?"
                         (Integer/parseInt (:id params))]))

(defn list-bookmark
  [request]
  (let [params (clojure.walk/keywordize-keys (-> request :query-params))]
    (if (empty? params)
      (list-all)
      (if (= (is-valid-param? params) true)
        (list-by-id)))))
