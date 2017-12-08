(ns leaflike.user.validator
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]
            [leaflike.config :refer [db-spec]]
            [leaflike.utils :refer [email-pattern
                                    alpha-num-pattern
                                    required]]))

(defn- is-email?
  [value]
  (and (required value)
       (nil? (re-matches email-pattern value))))

(defn- is-username?
  [value]
  (and (required value)
       (nil? (re-matches alpha-num-pattern value))))

(defn- is-password?
  [value]
  (required value))

(defn- count-member-if-exists
  [email username]
  (jdbc/query (db-spec) (-> (helpers/select :*)
                            (helpers/from :members)
                            (helpers/where [:or [:= :username username]
                                                [:= :email email]])
                            sql/format)))

(defn is-valid-registration?
  [body]
  (let [email (:email body)
        username (:username body)]

    (cond
      (not is-email?) "Email is invalid"
      (not is-username?) "Username is invalid"
      (not is-password?) "Password is invalid"
      (not (count-member-if-exists email username)) "User already exists"
      :else true)))
