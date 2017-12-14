(ns leaflike.middlewares
  (:require [buddy.auth.middleware :refer [wrap-authentication
                                           wrap-authorization]]
            [buddy.auth :refer [authenticated?
                                throw-unauthorized]]
            [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [session-auth-backend]]
            [ring.middleware.json :as json]
            [ring.middleware.params :as params]))

(defn wrap-unauthorized
  [handler]
  (fn [request]
    (when (not (authenticated? request))
      (throw-unauthorized))))

(def auth-middleware
  (comp #(wrap-authentication % session-auth-backend)
        #(wrap-authorization % session-auth-backend)
        wrap-unauthorized
        param/wrap-params
        #(json/wrap-json-body % {:keywords? true :bigdecimals? true})
        json/wrap-json-response))

(def home-middleware
  (comp params/wrap-params
        #(json/wrap-json-body % {:keywords? true :bigdecimals? true})
        json/wrap-json-response))

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
