(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.validator :as validator]
            [clojure.walk :as walk]))

(defn signup
  [{:keys [params] :as request}]
  (let [user (walk/keywordize-keys params)]
    (if (validator/valid-signup-details? user)
      (do (user-db/create-user user)
          (auth/signup-auth request (:username user)))
      (throw (ex-info "Invalid params" {:data params})))))

(defn login
  [{:keys [params] :as request}]
  (let [body   (walk/keywordize-keys params)
        member (validator/valid-login-details? body)]
    (if member
      (let [data {:auth-data        (-> member
                                        (assoc :username (str (:username member)))
                                        (dissoc :email :created_at))
                  :verify-password  (:password body)}]
        (auth/login-auth request data))
      (throw (ex-info "Invalid login credentials" {:type :invalid-login
                                                   :data params})))))

(defn logout
  [request]  
  (auth/logout-auth request))
