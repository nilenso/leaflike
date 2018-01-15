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
        member   (get-member-if-exists email username)]

    (cond
      (not email?)          {:error "Email is xinvalid"}
      (not username?)       {:error "Username is invalid"}
      (not password?)       {:error "Password is invalid"}
      (not (empty? member)) {:error "User already exists"}
      :else                 true)))

(defn valid-user?
  [body]
  (let [username (:username body)
        member   (get-member-auth-data username)]

    (cond
      (not username?)     false
      (not (nil? member)) (first member))))
