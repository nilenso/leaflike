(ns leaflike.user.spec
  (:require [leaflike.utils :refer [email-pattern
                                    alpha-num-pattern
                                    required]]
            [leaflike.user.db :refer [get-member-if-exists
                                      get-member-auth-data]]
            [clojure.spec.alpha :as s]))

(defn email?
  [value]
  (and (required value)
       (re-matches email-pattern value)))

(s/def ::email email?)

(defn username?
  [value]
  (and (required value)
       (re-matches alpha-num-pattern value)))

(s/def ::username username?)

(defn password?
  [value]
  (required value))

(s/def ::password password?)

(s/def ::signup-details (s/keys :req-un [::email ::username ::password]))

(defn valid-signup-details?
  [{:keys [email username] :as user}]
  (and (s/valid? ::signup-details user)
       (nil? (get-member-if-exists email username))))

(s/def ::login-details (s/keys :req-un [::username ::password]))

(defn valid-login-details?
  [{:keys [username password] :as user}]
  (if (s/valid? ::login-details user)
    (let [member (get-member-auth-data username)]
      (first member))
    false))
