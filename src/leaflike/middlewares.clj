(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorization]]
            [ring.util.response :as res])
  (:import clojure.lang.ExceptionInfo))

(defn handle-exception
  "In case of an ExceptionInfo caused by bad input, adds a `flash`
  message to be displayed to the user. If the `type` in the
  ExceptionInfo is not known, return a HTTP 500 error."
  [e]
  (let [info (ex-data e)
        error-type (:type info)]
    (case error-type
      :invalid-login (assoc (res/redirect "/login")
                            :flash {:error-msg "Invalid username/password"})
      ;; TODO: Use spec/explain-* fns to figure out which keys were invalid.
      :invalid-bookmark (assoc (res/redirect "/bookmarks/add")
                               :flash {:error-msg "Invalid bookmark"})
      ;; TODO: Use spec/explain-* fns to figure out what went wrong.
      :invalid-signup (assoc (res/redirect "/signup")
                             :flash {:error-msg "Invalid signup details"})
      {:status 500
       :body (.getMessage e)})))

(defn wrap-exception-handling
  "Catches an exception and returns the appropriate HTTP response."
  [handler-fn]
  (fn [request]
    (try (handler-fn request)
         (catch ExceptionInfo e
           (handle-exception e)))))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorization))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
