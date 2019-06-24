(ns leaflike.user.db
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]
            [leaflike.config :refer [db-spec]]
            [leaflike.utils :as utils]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]))

(defn get-user-if-exists
  ([username] (get-user-if-exists nil username))
  ([email username]
   (-> (jdbc/query (db-spec) (-> (helpers/select :*)
                                 (helpers/from :users)
                                 (helpers/where [:or [:= :username username]
                                                 [:= :email email]])
                                 sql/format))
       first)))



(defn get-user-ids-by-username
  [usernames]
  (jdbc/query (db-spec) (-> (helpers/select :id)
                            (helpers/from :users)
                            (helpers/merge-where [:in :username usernames])
                            sql/format)))

(defn get-username-by-user-id
  [id]
  (jdbc/query (db-spec) (-> (helpers/select :username)
                            (helpers/from :users)
                            (helpers/where [:= :id id])
                            sql/format)))

(defn get-user-auth-data
  ([identifier]
   (get-user-auth-data identifier :*))
  ([identifier coll]
   (jdbc/query (db-spec) (-> (helpers/select coll)
                             (helpers/from :users)
                             (helpers/where [:= :username identifier])
                             sql/format))))

(defn create-user
  [body]
  (jdbc/execute! (db-spec) (-> (helpers/insert-into :users)
                               (helpers/values [{:email      (:email body)
                                                 :username   (:username body)
                                                 :password   (hashers/encrypt (:password body))
                                                 :created_at (utils/get-timestamp)}])
                               sql/format)))
