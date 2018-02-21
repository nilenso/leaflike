(ns leaflike.bookmarks.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [honeysql.types  :as types]
            [honeysql.format :as fmt]
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

(defn count-bookmarks
  "Return number of bookmarks the user has."
  [{:keys [member-id tag] :as query}]
  (let [where-clause (build-where-clause (select-keys query [:member-id :tag :search-terms]))]
    (->> (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                                   (helpers/from :bookmarks)
                                   (helpers/where where-clause)
                                   sql/format))
         first
         :count)))

(defn fetch-bookmarks
  "Fetch a `limit` number of bookmarks starting from `offset`."
  [{:keys [member-id limit offset tag search-terms] :or {offset 0} :as query}]
  (println query)
  (let [where-clause (build-where-clause (select-keys query [:member-id :tag :search-terms]))]
    (jdbc/query (db-spec) (-> (helpers/select :*)
                              (helpers/from :bookmarks)
                              (helpers/where where-clause)
                              (helpers/limit limit)
                              (helpers/offset offset)
                              (helpers/order-by [:created_at :desc])
                              sql/format))))

#_(defn search-bookmarks
  "Search for `member_id`'s bookmarks whose title or URL contains `search_terms`"
  [{:keys [member-id search-terms]}]
  (let [ts-query (str/join " & " search-terms)]
    (jdbc/query (db-spec) (-> (helpers/select :*)
                              (helpers/from :bookmarks)
                              (helpers/where [:and
                                              [:= :member_id member-id]
                                              [:matches [(sql/call :concat :%to_tsvector.title :%to_tsvector.url)]
                                               [(sql/call :to_tsquery ts-query)]]])
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
