(ns leaflike.handler-utils
  (:require [leaflike.user.db :as user-db]))

(defn get-user
  [request]
  (let [username (get-in request [:session :username])]
    (first (user-db/get-member-auth-data username :id))))
