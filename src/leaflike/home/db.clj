(ns leaflike.home.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core     :as sql]
            [honeysql.helpers  :as helpers]
            [leaflike.utils    :as utils]
            [leaflike.config   :refer [db-spec]]))


(defn- build-where-clause
  [user-id from-date to-date type]
  (let [date-key (case type
                   :read :read_at
                   :favorite :favorite_at
                   :created_at)
        where-clause [:and [:= :user_id user-id]]
        where-clause (case type
                       :read (conj where-clause [:= true :read])
                       :favorite (conj where-clause [:= true :favorite])
                       where-clause)
        where-clause (if from-date
                       (let [from-date (utils/get-timestamp from-date)]
                         (conj where-clause [:<= from-date date-key]))
                       where-clause)
        where-clause (if to-date
                       (let [to-date (utils/get-timestamp to-date)]
                         (conj where-clause [:>= to-date date-key]))
                       where-clause)]
    where-clause))

(defn count-bookmarks
  "Return number of bookmarks the user has along with read/favorite during give time frame."
  [user-id from-date to-date]
  (let [where-clause-all (build-where-clause user-id from-date to-date :all)
        where-clause-read (build-where-clause user-id from-date to-date :read)
        where-clause-fav (build-where-clause user-id from-date to-date :favorite)
        count-all (->> (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                                                 (helpers/from :bookmarks)
                                                 (helpers/where where-clause-all)
                                                 sql/format))
                       first
                       :count)
        count-read (->> (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                                                  (helpers/from :bookmarks)
                                                  (helpers/where where-clause-read)
                                                  sql/format))
                        first
                        :count)
        count-fav (->> (jdbc/query (db-spec) (-> (helpers/select :%count.*)
                                                 (helpers/from :bookmarks)
                                                 (helpers/where where-clause-fav)
                                                 sql/format))
                       first
                       :count)]
    {:total count-all :read count-read :favorite count-fav}))