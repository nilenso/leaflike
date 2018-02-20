(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [honeysql.types  :as types]
            [clojure.string    :as str]
            [leaflike.config   :refer [db-spec]]))

;;; Parse java.sql.Array into a vector.
(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Array
  (result-set-read-column [val _ _]
    (into [] (.getArray val))))

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
  [{:keys [member-id]}]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:= :member_id member-id])
                            sql/format)))

(defn- build-where-clause
  [{:keys [member-id tag]}]
  (let [where-clause [:and [:= :member_id member-id]]
        where-clause (if tag
                       (conj where-clause [:= tag :%any.tags])
                       where-clause)]
    where-clause))

(defn count-bookmarks
  "Return number of bookmarks the user has."
  [{:keys [member-id tag] :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member-id :tag]))]
    (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                              (helpers/from :bookmarks)
                              (helpers/where where-clause)
                              sql/format))))

(defn fetch-bookmarks
  "Fetch a `limit` number of bookmarks starting from `offset`."
  [{:keys [member-id limit offset tag] :or {offset 0} :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member-id :tag]))]
    (jdbc/query (db-spec) (-> (helpers/select :*)
                              (helpers/from :bookmarks)
                              (helpers/where where-clause)
                              (helpers/limit limit)
                              (helpers/offset offset)
                              (helpers/order-by [:created_at :desc])
                              sql/format))))

(defn list-by-id
  [{:keys [id member-id]}]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:and [:= :id (Integer/parseInt id)]
                                            [:= :member_id member-id]])
                            sql/format)))

(defn delete
  [{:keys [id member-id]}]
  (jdbc/execute! (db-spec) (-> (helpers/delete-from :bookmarks)
                               (helpers/where [:and [:= :id (Integer/parseInt id)]
                                               [:= :member_id member-id]])
                               sql/format)))
