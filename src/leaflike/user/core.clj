(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.validator :as validator]))

(defn signup
  [request]

  (let [body      (clojure.walk/keywordize-keys (-> request :params))
        valid-reg (validator/valid-registration? body)]
    (if valid-reg
       (do (user-db/create-user body)
           (auth/signup-auth request (:username body)))
       {:error valid})))

(defn login
  [request]

    (let [body   (clojure.walk/keywordize-keys (-> request :params))
          member (validator/valid-user? body)]

      (when member
        (let [data {:auth-data        (-> member
                                          (assoc-in [:username] (str (:username member)))
                                          (dissoc   :email :created_on))

                    :verify-password  (:password body)}]

          (auth/login-auth request data)))))

(defn logout
  [request]
  (auth/logout-auth request))
