(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorization
                                        wrap-auth-response]]))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorization
      wrap-auth-response))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
