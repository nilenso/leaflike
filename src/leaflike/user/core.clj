(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.validator :as validator]))

(defn signup
  [request]

  (let [body  (clojure.walk/keywordize-keys (-> request :params))
        valid (validator/valid-registration? body)]
    (cond
      (true? valid) (do (user-db/create-user body)
                        (auth/signup-auth request (:username body)))

      :else {:error valid})))
