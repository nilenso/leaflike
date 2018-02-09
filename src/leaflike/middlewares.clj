(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorization]]
            [ring.util.response :as res])
  (:import clojure.lang.ExceptionInfo))

(defn handle-exception
  [e]
  (let [info (ex-data e)
        type (:type info)]
    (case type
      :invalid-login (res/redirect "/login?error=1")
      {:status 400
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
