(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorized
                                        wrap-unauthorized]]
            [ring.middleware.json :as json]
            [ring.middleware.params :as params]
            [leaflike.user.db :as user-db]))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      json/wrap-json-response
      (json/wrap-json-params {:keywords? true :bigdecimals? true})
      params/wrap-params
      wrap-authorized
      wrap-unauthorized))

(defn home-middleware
  [handler-fn]
  (-> handler-fn
      json/wrap-json-response
      (json/wrap-json-params {:keywords? true :bigdecimals? true})
      params/wrap-params))

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))