(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorized
                                        wrap-unauthorized]]
            [ring.middleware.json :as json]
            [ring.middleware.session :as session]
            [ring.middleware.session.memory :as mem]
            [ring.middleware.params :as params]
            [leaflike.user.db :as user-db]))

(defonce all-sessions (atom {}))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      json/wrap-json-response
      (json/wrap-json-params {:keywords? true :bigdecimals? true})
      params/wrap-params
      wrap-authorized
      wrap-unauthorized
      (session/wrap-session {:store (mem/memory-store all-sessions)})))

(defn home-middleware
  [handler-fn]
  (-> handler-fn
      json/wrap-json-response
      (json/wrap-json-params {:keywords? true :bigdecimals? true})
      params/wrap-params
      (session/wrap-session {:store (mem/memory-store all-sessions)})))

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
