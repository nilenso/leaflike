(ns leaflike.user.routes
  (:require [leaflike.user.core :as user-core]
            [leaflike.middlewares :refer [with-auth-middlewares]]))

(def user-routes
  {;; existing user
   "login"        {:post user-core/login
                   :get  user-core/login-page}
   "logout"       (with-auth-middlewares  {:get user-core/logout})
   ;; new user
   "signup"       {:post user-core/signup
                   :get  user-core/signup-page}})
