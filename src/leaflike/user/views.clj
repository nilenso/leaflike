(ns leaflike.user.views
  (:require [hiccup.form :as f]
            [hiccup.element :refer [link-to]]))

#_(defn login-form
  []
  [:div {:class "well"}
   [:form {:novalidate "" :role "form"}
     [:post "/login"]
     [:div {:class "form-group"}
      (f/label {:class "control-label"} "username" "Username")
      (f/text-field {:class "form-control" :placeholder "Username"} "username")]
     [:div {:class "form-group"}
      (f/label {:class "control-label"} "password" "Password")
      (f/password-field {:class "form-control" :placeholder "Password"} "password")]
     [:div
      (f/submit-button {:class "btn btn-primary" :on-click "console.log(\"ma\")"} "Login")]]])

(defn login-form
  []
  [:div {:class "well"}])

#_(link-to {:class "btn btn-primary"} "/" "Take me to Home")

#_(defn register-form
  []
  (hf/form-to [:post "/create-user"]
              (hf/text-field     {:placeholder "Username"} "username")
              (hf/email-field    {:placeholder "Email"}    "email")
              (hf/password-field {:placeholder "Password"} "password")
              (hf/submit-button  "Register")))

#_(defn auth-page-view
  []
  (hc/html
   [:div#register (register-form)]
   [:div#login    (login-form)]))
