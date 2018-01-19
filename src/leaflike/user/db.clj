(ns leaflike.user.db
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]
            [leaflike.config :refer [db-spec]]
            [leaflike.utils :as utils]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]))

(defn get-member-if-exists
  [email username]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :members)
                            (helpers/where [:or [:= :username username]
                                                [:= :email email]])
                            sql/format)))

(defn get-member-auth-data
  ([identifier]
   (jdbc/query (db-spec) (-> (helpers/select :*)
                             (helpers/from :members)
                             (helpers/where [:= :username identifier])
                             sql/format)))

  ([identifier coll]
   (jdbc/query (db-spec) (-> (helpers/select coll)
                             (helpers/from :members)
                             (helpers/where [:= :username (name identifier)])
                             sql/format))))

(defn create-user
  [body]
  (jdbc/execute! (db-spec) (-> (helpers/insert-into :members)
                               (helpers/values [{:email      (:email body)
                                                 :username   (:username body)
                                                 :password   (hashers/encrypt (:password body))
                                                 :created_at (utils/get-timestamp)}])
                               sql/format)))
