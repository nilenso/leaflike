(ns leaflike.user.routes
  (:require [leaflike.user.core :as user-core]
            [leaflike.user.views :as views]
            [leaflike.middlewares :refer [with-auth-middlewares]]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.util.response :as res]
            [leaflike.layout :as layout]))

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
  (user-core/logout request))

(defn login-page
  [request]
  (-> (res/response (layout/application 
                        "Login" 
                        (views/login-form anti-forgery/*anti-forgery-token*)))
      (assoc :headers {"Content-Type" "text/html"})
      (assoc :status 200)))

(defn signup-page
  [request]
  (-> (res/response (layout/application 
                        "Signup" 
                        (views/signup-form anti-forgery/*anti-forgery-token*)))
      (assoc :headers {"Content-Type" "text/html"})
      (assoc :status 200)))

(def user-routes
  {;; existing user
   "login"        {:post login
                   :get  login-page}
   "logout"       (with-auth-middlewares  {:post logout})
   ;; new user
   "signup"       {:post signup
                   :get  signup-page}})
