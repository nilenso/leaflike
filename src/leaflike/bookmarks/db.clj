(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.config :refer [db-spec]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]
            [honeysql.types :as types]))

(defn format-tags
  [{:keys [tags] :as params}]
  ;; check if tag exists
  ;; check if tags is nil?
  (if (nil? tags)
    params
    (update params :tags #(types/array %))))

(defn create
  [params]
  (jdbc/execute! (db-spec) (-> (helpers/insert-into :bookmarks)
                               (helpers/values [(format-tags params)])
                                sql/format)))
  
(defn list-all
  [{:keys [member_id]}]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:= :member_id member_id])
                            sql/format)))

(defn list-by-id
  [{:keys [id member_id]}]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:and [:= :id (Integer/parseInt id)]
                                            [:= :member_id member_id]])
                            sql/format)))

(defn delete
  [{:keys [id member_id]}]
  (jdbc/execute! (db-spec) (-> (helpers/delete-from :bookmarks)
                               (helpers/where [:and [:= :id (Integer/parseInt id)]
                                               [:= :member_id member_id]])
                               sql/format)))
