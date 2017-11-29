(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [ring.util.response :as res]))

(defn welcome [_]
  (res/response "Welcome to leaflike"))

(defn yay [_]
  (res/response "Yay!"))

(defn ping [request]
  (res/response {:ping (-> request :route-params :ping)}))

(def routes  {""              {:get welcome}
              "yay"           {:get yay}
              ["ping/" :ping] {:get ping}})

(def handler
  (bidi/make-handler ["/" routes]))
