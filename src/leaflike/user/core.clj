(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.validator :as validator]))

(defn signup
  [request]
  (let [body      (clojure.walk/keywordize-keys (:params request))
        valid-reg (validator/valid-registration? body)]
    (if valid-reg
      (do (user-db/create-user body)
          (auth/signup-auth request (:username body)))
      valid-reg)))

(defn login
  [request]
  (let [body   (clojure.walk/keywordize-keys (:params request))
        member (validator/valid-user? body)]
    (if member
      (let [data {:auth-data        (-> member
                                        (assoc-in [:username] (str (:username member)))
                                        (dissoc   :email :created_at))
                  :verify-password  (:password body)}]
        (auth/login-auth request data))
      (auth/throw-unauthorized 401))))

(defn logout
  [request]  
  (auth/logout-auth request))
