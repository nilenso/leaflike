(ns leaflike.user.validator
  (:require [leaflike.utils :refer [email-pattern
                                    alpha-num-pattern
                                    required]]
            [leaflike.user.db :refer [get-member-if-exists]]))

(defn- email?
  [value]
  (and (required value)
       (nil? (re-matches email-pattern value))))

(defn- username?
  [value]
  (and (required value)
       (nil? (re-matches alpha-num-pattern value))))

(defn- password?
  [value]
  (required value))

(defn valid-registration?
  [body]
  (let [email (:email body)
        username (:username body)]

    (cond
      (not email?) "Email is invalid"
      (not username?) "Username is invalid"
      (not password?) "Password is invalid"
      (not (get-member-if-exists email username)) "User already exists"
      :else true)))

(defn valid-user?
  [body]
  (let [email (:email body)
        username (:username body)
        member (get-member-if-exists email username)]

    (cond
      (not email?) "Email is invalid"
      (not username?) "Username is invalid"
      (not (nil? member)) true)))
