(ns leaflike.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.validator :refer [is-valid-bookmark? is-valid-params?]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers])
  (:import [java.util Date TimeZone]
           [java.text SimpleDateFormat]
           [java.sql Timestamp]))

(defn get-timestamp
  []
  (let [date (Date.)]
    (TimeZone/setDefault (TimeZone/getTimeZone "UTC"))
    (.format (SimpleDateFormat. "yyyy-mm-dd hh:mm:ss") date)
    (Timestamp. (.getTime date))))

(defn db-spec
  []
  {:connection-uri "jdbc:postgresql://localhost:5432/leaflike"})

(defn add-created-at
  [body]
  (assoc body :created_at (get-timestamp)))

(defn create-bookmark
  [request]
  (let [body (-> request :body)
        bkm (add-created-at body)]
    (if (is-valid-bookmark? body)
      (jdbc/insert! (db-spec) (-> (helpers/insert-into :bookmarks)
                                  (helpers/values [body])
                                  sql/format))
      ; else
      {:error "Invalid Data"})))

(defn list-all
  []
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            sql/format)))

(defn list-by-params
  [params]
  (let [id (Integer/parseInt (:id params))
        title (:title params)]
    (jdbc/query (db-spec) (-> (helpers/select :*)
                              (helpers/from :bookmarks)
                              (helpers/merge-where (when (> id 0)
                                             [:= :id id]))
                              (helpers/merge-where (when-not (nil? title)
                                                     ["like" :title (str \% title \%)]))
                              sql/format))))

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
      (jdbc/delete! (db-spec) (-> (helpers/delete-from :bookmarks)
                                  (helpers/merge-where [:= :id (Integer/parseInt (:id params))])
                                  sql/format))
      {:error "Invalid params"})))
