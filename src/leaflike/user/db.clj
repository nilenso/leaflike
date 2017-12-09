(ns leaflike.user.db
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]
            [leaflike.config :refer [db-spec]]
            [leaflike.utils :refer [get-timestamp]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers]
            [leaflike.user.validator :refer [valid-registration?]]))

(defn- create-user
  [body]
  (jdbc/execute! (db-spec) (-> (helpers/insert-into :members)
                               (helpers/values [{:email (:email body)
                                                 :username (:username body)
                                                 :password (hashers/encrypt (:password body))
                                                 :created_at (get-timestamp)}])
                               sql/format)))

(defn signup
  [request]
  (let [body (-> request :body)
        valid (valid-registration? body)]

    (cond
      (true? valid) (create-user body)
      :else         {:error valid})))

(defn login
  [request])

(defn logout
  [request])
