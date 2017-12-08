(ns leaflike.user.routes
  (:require [leaflike.user.db :as udb]))

(defn signup
  [request]
  (udb/signup request))

(def user-routes
  {"signup"       {:post signup}})
