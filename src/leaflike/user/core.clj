(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.validator :as validator]))

(defn signup
  [request]

  (let [body (clojure.walk/keywordize-keys (-> request :params))
        valid (validator/valid-registration? body)]
    (cond

      (true? valid) (->  (user-db/create-user body)
                         (auth/login-auth request body))

      :else         {:error valid})))
