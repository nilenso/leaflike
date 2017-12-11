(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.utils :as utils]
            [leaflike.config :refer [db-spec]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]))

(defn create-bookmark
  [body]
  (utils/add-created-at body)
  (jdbc/execute! (db-spec) (-> (helpers/insert-into :bookmarks)
                               (helpers/values [body])
                               sql/format)))

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

(defn delete-bookmark
  [params]
  (jdbc/delete! (db-spec) (-> (helpers/delete-from :bookmarks)
                              (helpers/merge-where [:= :id (Integer/parseInt (:id params))])
                              sql/format)))
