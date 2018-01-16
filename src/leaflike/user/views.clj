(ns leaflike.user.views
  (:require [hiccup.form :as hf]
            [hiccup.core :as hc]))

(defn register-form
  []
  (hf/form-to [:post "/create-user"]
              (hf/text-field     {:placeholder "Username"} "username")
              (hf/email-field    {:placeholder "Email"}    "email")
              (hf/password-field {:placeholder "Password"} "password")
              (hf/submit-button  "Register")))

(defn login-form
  []
  (hf/form-to [:post "/login"]
              (hf/text-field     {:placeholder "Username/Email"} "username")
              (hf/password-field {:placeholder "Password"}       "password")
              (hf/submit-button  "Login")))

(defn auth-page-view
  []
  (hc/html
   [:div#register (register-form)]
   [:div#login    (login-form)]))