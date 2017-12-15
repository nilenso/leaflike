(ns leaflike.user.routes
  (:require [leaflike.user.core :as user-core]
            [leaflike.middlewares :refer [with-home-middlewares
                                          with-auth-middlewares]]
            [ring.util.response :as res]))

(defn signup
  [request]
  (user-core/signup request))

(defn edit
  [request]
  ;;modify user
  )

(defn login
  [request]
  ;; authenticate
  )

(defn logout
  [request]
  ;; logout
  )

(defn login-page
  [request]
  (-> (res/resource-response "login.html" {:root "public"})
      (assoc :headers {"Content-Type" "text/html"})
      (assoc :status 200)))

(def api-routes
  {;; existing user
   ;;"logout"       (with-auth-middlewares  {:post logout})
   ;;"login"        (with-home-middlewares  {:post login})
   ;;"edit-user"    (with-auth-middlewares  {:post edit})
   ;; new user
   "create-user"  (with-home-middlewares  {:post signup})})

(def html-routes
  {"login.html"   (with-home-middlewares {:get login-page})})

(def user-routes
  (merge api-routes html-routes))
