(ns leaflike.user.routes
  (:require [leaflike.user.core :as user-core]))

(defn signup
  [request]
  (user-core/signup request))

(def user-routes
  {"signup"       {:post signup}})
