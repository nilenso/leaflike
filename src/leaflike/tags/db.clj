(ns leaflike.tags.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [honeysql-postgres.format :as pg-fmt]
            [honeysql-postgres.helpers :as pg-helpers]
            [leaflike.config   :refer [db-spec]]))

(defn create
  [tags]
  (jdbc/execute! (db-spec)
                 (-> (helpers/insert-into :tags)
                     (helpers/values (map (fn [n] {:name n}) tags))
                     (pg-helpers/upsert (pg-helpers/do-nothing
                                         (pg-helpers/on-conflict)))
                     sql/format)))

(defn fetch-tags
  [{:keys [member-id]}]
  (jdbc/query (db-spec)
              (-> (helpers/select :*)
                  (helpers/from :tags)
                  (helpers/where [:in :id
                                  (-> (helpers/select :tag_id)
                                      (helpers/from [:bookmark_tag :bt]
                                                    [:bookmarks :b])
                                      (helpers/where [:and [:= :member_id member-id]
                                                      [:= :bt.bookmark_id :b.id]])
                                      (helpers/group :bt.tag_id))])
                  (helpers/order-by :name)
                  sql/format)))

(defn list-bookmarks
  "List bookmark tagged with `tag-name`"
  [tag-name]
  (jdbc/query (db-spec)
              (-> (helpers/select :b.*)
                  (helpers/from [:bookmarks :b] [:tags :t] [:bookmark_tag :bt])
                  (helpers/where [:and
                                  [:= :t.name tag-name]
                                  [:= :b.id :bt.bookmark_id]
                                  [:= :t.id :bt.tag_id]])
                  sql/format)))
