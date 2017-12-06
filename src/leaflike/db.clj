(ns leaflike.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.validator :refer [is-valid-bookmark? is-valid-params?]]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all])
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
    (if (is-valid-bookmark? body)
      (jdbc/insert! (db-spec) :bookmarks bkm)
      ; else
      {:error "Invalid Data"})))

(defn list-all
  []
  (jdbc/query (db-spec) (-> (select :*)
                            (from :bookmarks)
                            sql/format)))

(defn list-by-params
  [params]
  (jdbc/query (db-spec) (-> (select :*)
                            (from :bookmarks)
                            (merge-where (let [id (Integer/parseInt (:id params))]
                                           (if (> id 0)
                                               [:= :id id])))
                            (merge-where (let [title (:title params)]
                                           (if-not (nil? title)
                                                   [:= :title title])))
                            sql/format)))

(defn list-bookmark
  [request]
  (let [params (clojure.walk/keywordize-keys (-> request :query-params))]
    (if (empty? params)
      (list-all)
      (if (is-valid-params? params)
        (list-by-params params)
        {:error "Invalid Params"}))))

(defn delete-bookmark
  [request]
  (let [params (-> request :route-params)]
    (if  (is-valid-params? params)
      (jdbc/delete! (db-spec) :bookmarks ["id = ?" (Integer/parseInt (:id params))]))))
