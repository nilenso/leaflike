(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [honeysql.types    :as types]
            [clojure.string    :as str]
            [leaflike.config   :refer [db-spec]]))

(defn format-tags
  [{:keys [tags] :as bookmark}]
  ;; check if tag exists
  ;; check if tags is nil?
  (if (nil? tags)
    bookmark
    (assoc bookmark)))

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

(defn count-bookmarks
  "Return number of bookmarks the user has."
  [{:keys [member_id]}]
  (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                            (helpers/from :bookmarks)
                            (helpers/where [:= :member_id member_id])
                            sql/format)))

(defn fetch-bookmarks
  "Fetch a `limit` number of bookmarks starting from `offset`."
  [{:keys [member_id limit offset] :or {offset 0}}]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:= :member_id member_id])
                            (helpers/limit limit)
                            (helpers/offset offset)
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
