(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorized
                                        wrap-unauthorized]]
            [ring.middleware.json :as json]
            [leaflike.user.db :as user-db]))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorized
      wrap-unauthorized))

(defn home-middleware
  [handler-fn]
  handler-fn)

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
