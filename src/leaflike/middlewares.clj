(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorized
                                        wrap-unauthorized]]))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorized
      wrap-unauthorized))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
