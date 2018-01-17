(ns leaflike.user.validator
  (:require [leaflike.utils :refer [email-pattern
                                    alpha-num-pattern
                                    required]]
            [leaflike.user.db :refer [get-member-if-exists
                                      get-member-auth-data]]))

(defn email?
  [value]
  (and (required value)
       (re-matches email-pattern value)))

(defn username?
  [value]
  (and (required value)
       (re-matches alpha-num-pattern value)))

(defn password?
  [value]
  (required value))

(defn valid-registration?
  [body]
  (let [email    (:email body)
        username (:username body)
        password (:password body)]

    (cond
      (not (email? email))              {:error "Email is invalid"}
      (not (username? username))        {:error "Username is invalid"}
      (not (password? password))        {:error "Password is invalid"}
      (not-empty (get-member-if-exists email username)) {:error "User already exists"}
      :else                             true)))

(defn valid-user?
  [body]
  (let [username (:username body)
        password (:password body)
        member   (get-member-auth-data username)]

    (cond
      (not (username? username)) false
      (not (password? password)) false
      (not-empty member)         member)))
