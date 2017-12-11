(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [clojure.java.io :as io]
            [leaflike.user.validator :as validator]))

(defn signup
  [request]
  (let [body (clojure.walk/keywordize-keys (-> request :params))
        valid (validator/valid-registration? body)]
    (cond
      (true? valid) {:count (user-db/create-user body)}
      :else         {:error valid})))
