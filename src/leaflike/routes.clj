(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [ring.util.response :as res]
            [leaflike.db :as db]))

(defn ring-response-middleware [handler]
  (fn [request]
    (let [response (handler request)]
      (res/response response))))

(defn welcome [_]
  {:message "Welcome to Leaflike"})

(defn ping [request]
  {:ping (-> request :route-params :ping)})

(defn create-bookmark
  [request]
  (db/create-bookmark request))

(def routes  {""                 {:get welcome}
              ["ping/" :ping]    {:get ping}
              "create-bookmark"  {:post create-bookmark}})

(def handler
  (bidi/make-handler ["/" routes]))
