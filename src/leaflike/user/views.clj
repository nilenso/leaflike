(ns leaflike.user.views
  (:require [hiccup.form :as f]
            [hiccup.element :refer [link-to]]))

(defn login-form
  []
  [:div {:class "well"}
   [:h3 {:class "text-success"} "Login"]
   (f/form-to {:role "form" :novalidate ""}
              [:post "/login"]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "username" "Username")
               (f/text-field {:class "form-control" :placeholder "Username"} "username")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "password" "Password")
               (f/password-field {:class "form-control" :placeholder "Password"} "password")]
              [:div {:class "form-group"}
               (f/submit-button {:class "btn btn-primary"} "Login")])])

(defn signup-form
  []
  [:div {:class "well"}
   [:h3 {:class "text-success"} "Signup"]
   (f/form-to {:role "form" :novalidate ""}
              [:post "/signup"]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "email" "Email")
               (f/email-field {:class "form-control" :placeholder "Email"} "email")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "username" "Username")
               (f/text-field {:class "form-control" :placeholder "Username"} "username")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "password" "Password")
               (f/password-field {:class "form-control" :placeholder "Password"} "password")]
              [:div {:class "form-group"}
               (f/submit-button {:class "btn btn-primary"} "Signup")])])
