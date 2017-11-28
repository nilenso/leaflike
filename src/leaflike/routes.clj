(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [ring.util.response :as res]))

(defn yay [request]
  (res/response "Yay!"))

(defn welcome [request]
  (res/response "Welcome to leaflike"))

(def urls
  {{:get "" welcome
         "yay" yay}})

(def handler
  (bidi/make-handler ["/" urls]))
