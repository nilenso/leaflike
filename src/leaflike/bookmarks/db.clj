(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [honeysql.types  :as types]
            [honeysql.format :as fmt]
            [honeysql-postgres.helpers :as pg-helpers]
            [clojure.string    :as str]
            [leaflike.config   :refer [db-spec]]))

;;; Parse java.sql.Array into a vector.
(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Array
  (result-set-read-column [val _ _]
    (into [] (.getArray val))))

;;; Introduce || and @@ syntax to honeysql. This is called `matches`
;;; because Clojure keywords cannot have a `@` character.
(defmethod fmt/fn-handler "matches" [_ tsvector tsquery]
  (str (fmt/to-sql tsvector)
       " @@ "
       (fmt/to-sql tsquery)))

(defmethod fmt/fn-handler "concat" [_ left right]
  (str (fmt/to-sql left)
       " || ' ' || "
       (fmt/to-sql right)))

(defmethod fmt/fn-handler "to_tsquery" [_ query]
  (str "to_tsquery(" (fmt/to-sql query) ")"))

(defn- seq->pgarray
  [s]
  (when s (types/array s)))


(defn tag-bookmark
  [bookmark-id tags]
  (jdbc/execute! (db-spec)
                 (-> (helpers/insert-into :bookmark_tag)
                     (helpers/columns :bookmark_id :tag_id)
                     (helpers/query-values
                      (-> (helpers/select :bookmark_id :tag_id)
                          (helpers/from
                           [(helpers/select [bookmark-id :bookmark_id]) :_bookmark_id]
                           [(-> (helpers/select [:id :tag_id])
                                (helpers/from :tags)
                                (helpers/where [:in :name tags]))
                            :_tag_id])))
                     sql/format)))

(defn remove-all-tags
  "Remove all tags from bookmark"
  [bookmark-id]
  (jdbc/execute! (db-spec)
                 (-> (helpers/delete-from :bookmark_tag)
                     (helpers/where [:= :bookmark_id bookmark-id])
                     sql/format)))

(defn create
  [bookmark]
  (-> (jdbc/query (db-spec)
                  (-> (pg-helpers/insert-into-as :bookmarks :b)
                      (helpers/values [bookmark])
                      (pg-helpers/returning :b.id)
                      sql/format))
      first
      :id))

;;; NOTE: Named `update-bookmark` to avoid shadowing `update`
(defn update-bookmark
  [bookmark-id member-id updated-keys]
  (let [updated-keys (select-keys updated-keys [:title :url])]
    (jdbc/execute! (db-spec)
                   (-> (helpers/update :bookmarks)
                       (helpers/sset updated-keys)
                       (helpers/where [:and [:= :id bookmark-id]
                                       [:= :member_id member-id]])
                       sql/format))))

(defn- build-where-clause
  [{:keys [member-id tag search-terms]}]
  (let [where-clause [:and [:= :member_id member-id]]
        where-clause (if tag
                       (conj where-clause [:= tag :%any.tags])
                       where-clause)
        where-clause (if search-terms
                       (let [ts-query (str/join " & " search-terms)]
                         (conj where-clause
                               [:matches [(sql/call :concat :%to_tsvector.title :%to_tsvector.url)]
                                [(sql/call :to_tsquery ts-query)]]))
                       where-clause)]
    where-clause))


(defn all-bookmarks-map
  [member-id]
  (-> (helpers/select :b.id :b.title :b.url :b.created_at
                      :b.member_id
                      [(sql/call :array_remove :%array_agg.t.name :null) :tags])
      (helpers/from [:bookmarks :b])
      (helpers/left-join [:bookmark_tag :bt] [:= :b.id :bt.bookmark_id]
                         [:tags :t] [:= :bt.tag_id :t.id])
      (helpers/where [:= :b.member_id member-id])
      (helpers/group :b.id)))


(defn count-bookmarks
  "Return number of bookmarks the user has."
  [{:keys [member-id tag] :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member-id :tag :search-terms]))]
    (->> (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                                   (helpers/from [(all-bookmarks-map member-id) :member-bookmarks])
                                   (helpers/where where-clause)
                                   sql/format))
         first
         :count)))

(defn fetch-bookmark
  [bookmark-id member-id]
  (-> (jdbc/query (db-spec)
                  (-> (helpers/select :b.url :b.title :b.created_at :b.id :b.member_id
                                      [(sql/call :array_remove :%array_agg.t.name :null) :tags])
                      (helpers/from [:bookmarks :b])
                      (helpers/left-join [:bookmark_tag :bt] [:= :b.id :bt.bookmark_id]
                                         [:tags :t] [:= :bt.tag_id :t.id])
                      (helpers/where [:and [:= :b.id bookmark-id]
                                      [:= :b.member_id member-id]])
                      (helpers/group :b.id)
                      sql/format))
      first))


(defn fetch-bookmarks
  [{:keys [member-id limit offset tag search-terms] :or {offset 0} :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member-id
                                                             :tag
                                                             :search-terms]))]
    (jdbc/query (db-spec)
                (-> (helpers/select :*)
                    ;; sub-query gets all of member's bookmarks joined with tags
                    (helpers/from [(all-bookmarks-map member-id) :member-bookmarks])
                    (helpers/where where-clause)
                    (helpers/limit limit)
                    (helpers/offset offset)
                    (helpers/order-by [:created_at :desc])
                    sql/format))))

(defn fetch-bookmarks-for-user [member-id]
  (jdbc/query (db-spec)
              (-> (helpers/select :*)
                  (helpers/from :bookmarks)
                  (helpers/where [:= :member_id member-id])
                  sql/format)))

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
                               (helpers/where [:and [:= :id id]
                                               [:= :member_id member-id]])
                               sql/format)))
