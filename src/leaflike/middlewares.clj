(ns leaflike.middlewares
  (:require [buddy.auth.middleware :refer [wrap-authentication]]
            [clojure.algo.generic.functor :refer [fmap]]
            [ring.util.response :as res]
            [leaflike.user.auth :refer [basic-auth-backend]]
            [ring.middleware.json :as json]
            [ring.middleware.params :as params]))

(defn wrap-basic-auth
  "Middleware used on routes requiring basic authentication"
  [handler]
  (wrap-authentication handler basic-auth-backend))

(def home-middleware
  (comp params/wrap-params
        json/wrap-json-response))

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))
