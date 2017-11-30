(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [ring.util.response :as res]
            [leaflike.db :as db]))

(defn response
  [res]
  (res/response res))

(defn welcome [_]
  (response {:message "Welcome to Leaflike"}))

(defn ping [request]
  (response {:ping (-> request :route-params :ping)}))

(defn create-bookmark
  [request]
  (response (db/create-bookmark request)))

(def routes  {""                 {:get welcome}
              ["ping/" :ping]    {:get ping}
              "create-bookmark"  {:post create-bookmark}})

(def handler
  (bidi/make-handler ["/" routes]))
