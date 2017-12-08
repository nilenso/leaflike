(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.bookmarks.validator :refer [is-valid-bookmark? is-valid-params?]]
            [leaflike.bookmarks.utils :as utils]
            [leaflike.config :refer [db-spec]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]))

(defn create-bookmark
  [request]
  (let [body (-> request :body)]
    (utils/add-created-at body)
    (if (is-valid-bookmark? body)
      (jdbc/execute! (db-spec) (-> (helpers/insert-into :bookmarks)
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
