(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [honeysql.types  :as types]
            [clojure.string    :as str]
            [leaflike.config   :refer [db-spec]]))

(defn- seq->pgarray
  [s]
  (when s (types/array s)))

(defn create
  [bookmark]
  (let [bookmark (update bookmark :tags seq->pgarray)]
    (jdbc/execute! (db-spec) (-> (helpers/insert-into :bookmarks)
                                 (helpers/values [bookmark])
                                 sql/format))))

(defn list-all
  [{:keys [member_id]}]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:= :member_id member_id])
                            sql/format)))

(defn- build-where-clause
  [{:keys [member_id tag]}]
  (let [where-clause [:and [:= :member_id member_id]]
        where-clause (if tag
                       (conj where-clause [:= tag :%any.tags])
                       where-clause)]
    where-clause))

(defn count-bookmarks
  "Return number of bookmarks the user has."
  [{:keys [member_id tag] :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member_id :tag]))]
    (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                              (helpers/from :bookmarks)
                              (helpers/where where-clause)
                              sql/format))))

(defn fetch-bookmarks
  "Fetch a `limit` number of bookmarks starting from `offset`."
  [{:keys [member_id limit offset tag] :or {offset 0} :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member_id :tag]))]
    (jdbc/query (db-spec) (-> (helpers/select :*)
                              (helpers/from :bookmarks)
                              (helpers/where where-clause)
                              (helpers/limit limit)
                              (helpers/offset offset)
                              sql/format))))

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
