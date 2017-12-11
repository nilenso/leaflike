(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [leaflike.user.db :refer [get-member-auth-data]]))

(defn- user-auth-data
  "Identifier could be username or email.
   They are stored as CITEXT in postgres."
  [identifier]
  (let [auth-data (get-member-auth-data identifier)]
    (when-not (nil? auth-data)
      {:user-data (-> auth-data
                      (assoc-in [:username] (str (:username auth-data)))
                      (assoc-in [:email]    (str (:email auth-data)))
                      (dissoc   :created_on)
                      (dissoc   :password))
       :password (:password auth-data)})))

(defn- basic-auth
  "Determines that if the username and password
   required to authorise a user are valid.
   Returns a value which is added to the request
   with keyword :identity. Ref : https://goo.gl/1aiqZQ
   Username could be a valid username or email id"
  [request auth-data]
  (let [identifier  (:username auth-data)
        password    (:password auth-data)
        auth-data   (user-auth-data identifier)]
    (if (and auth-data (hashers/check password (:password auth-data)))
      (:user-data auth-data)
      false)))

(def basic-auth-backend
  "Use as the authentication
   function for the http-basic-backend"
  (http-basic-backend {:authfn basic-auth}))
