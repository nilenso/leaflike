(ns leaflike.user.routes
  (:require [leaflike.user.core :as user-core]
            [leaflike.user.views :as views]
            [leaflike.middlewares :refer [with-home-middlewares
                                          with-auth-middlewares]]
            [ring.util.response :as res]))

(defn signup
  [request]
  (user-core/signup request))

(defn login
  [request]
  ;; authenticate
  (user-core/login request))


(defn logout
  [request]
  ;; logout
  (user-core/logout))

(defn login-page
  [request]
  (-> (res/response (views/auth-page-view))
      (assoc :headers {"Content-Type" "text/html"})
      (assoc :status 200)))

(def user-routes
  {;; existing user
   "login"        (with-home-middlewares  {:post login
                                           :get  login-page})
   "logout"       (with-auth-middlewares  {:post logout})
   ;; new user
   "create-user"  (with-home-middlewares  {:post signup})})
