(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorization]])
  (:import clojure.lang.ExceptionInfo))

(defn wrap-exception-handling
  "Catches an exception and returns the appropriate HTTP response."
  [handler-fn]
  (fn [request]
    (try (handler-fn request)
         (catch ExceptionInfo e
           {:status 400
            :body (.getMessage e)}))))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorization))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
