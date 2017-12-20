(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [leaflike.config :refer [db-spec]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]))

(defn create
  [body]
  (jdbc/execute! (db-spec) (-> (helpers/insert-into :bookmarks)
                               (helpers/values [body])
                               sql/format)))

(defn list-all
  [member_id]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :bookmarks)
                            (helpers/where [:= :member_id member_id])
                            sql/format)))

(defn list-by-id
  [params]
  (let [id        (Integer/parseInt (:id params))
        member_id (:member_id params)]
    (jdbc/query (db-spec) (-> (helpers/select :*)
                              (helpers/from :bookmarks)
                              (helpers/where [:and [:= :id id]
                                              [:= :member_id member_id]])
                              sql/format))))

(defn delete
  [params]
  (let [id        (Integer/parseInt (:id params))
        member_id (:member_id params)]
    (jdbc/delete! (db-spec) (-> (helpers/delete-from :bookmarks)
                                (helpers/where [:and [:= :id id]
                                                [:= :member_id member_id]])
                                sql/format))))
