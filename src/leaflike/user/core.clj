(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [clojure.java.io :as io]
            [leaflike.user.validator :as validator]))

(defn signup
  [request]
  (let [body (-> request :body)
        valid (validator/valid-registration? body)]

    (cond
      (true? valid) (user-db/create-user body)
      :else         {:error valid})))
