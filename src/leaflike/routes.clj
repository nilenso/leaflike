(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [ring.util.response :as res]
            [leaflike.db :as db]))

(defn yay [request]
  (res/response "Yay!"))

(defn welcome [req]
  (res/response "Welcome to leaflike"))

(defn create-bookmark [req]
  (let [body (:body req)]
    (res/response (db/create-bookmark body))))

(def urls
  {{:get "" welcome
         "yay" yay}
   {:post "create_bookmark" create-bookmark}})

(def handler
  (bidi/make-handler ["/" urls]))
