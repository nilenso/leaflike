(ns leaflike.middlewares
  (:require [buddy.auth.middleware :refer [wrap-authentication
                                           wrap-authorization]]
            [clojure.algo.generic.functor :refer [fmap]]
            [ring.util.response :as res]
            [leaflike.user.auth :refer [session-auth-backend]]
            [ring.middleware.json :as json]
            [ring.middleware.params :as params]))

(defn wrap-session-auth
  "Middleware used on routes requiring basic authentication"
  [handler]
  (wrap-authentication handler session-auth-backend)
  (wrap-authorization handler session-auth-backend))

(def home-middleware
  (comp params/wrap-params
        #(json/wrap-json-body % {:keywords? true :bigdecimals? true})
        json/wrap-json-response))

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))

(defn with-auth-middlewares
  [route-map]
  (comp (with-home-middlewares route-map)
        wrap-session-auth))
