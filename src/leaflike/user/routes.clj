(ns leaflike.user.routes
  (:require [leaflike.user.core :as user-core]
            [leaflike.middlewares :refer [with-home-middlewares]]
            [ring.util.response :as res]))

(defn show-index
  []
  (res/content-type (res/resource-response "index.html" {:root "public"}) "text/html"))

(defn signup
  [request]
  (let [response (user-core/signup request)]

    (cond
      (contains? response :error) (res/response response)
      :else (show-index))))

(defn login-page
  [request]
  (res/content-type (res/resource-response "login.html" {:root "public"}) "text/html"))

(def user-routes
  {"signup"        (with-home-middlewares
                     {:post signup})
   "login"         (with-home-middlewares
                     {:get login-page})})
