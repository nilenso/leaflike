(ns leaflike.user.views
  (:require [hiccup.form :as hf]
            [hiccup.core :as hc]))

(defn register-form
  [anti-forgery-token]
  (hf/form-to [:post "/create-user"]
              (hf/text-field     {:placeholder "Username"} "username")
              (hf/email-field    {:placeholder "Email"}    "email")
              (hf/password-field {:placeholder "Password"} "password")
              (hf/submit-button  "Register")
              (hf/hidden-field {:value anti-forgery-token} "__anti-forgery-token")))

(defn login-form
  [anti-forgery-token]
  (hf/form-to [:post "/login"]
              (hf/text-field     {:placeholder "Username/Email"} "username")
              (hf/password-field {:placeholder "Password"}       "password")
              (hf/submit-button  "Login")
              (hf/hidden-field {:value anti-forgery-token} "__anti-forgery-token")))

(defn auth-page-view
  [anti-forgery-token]
  (hc/html
   [:div#register (register-form anti-forgery-token)]
   [:div#login    (login-form anti-forgery-token)]))
