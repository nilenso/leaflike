(ns leaflike.user.views
  (:require [hiccup.form :as f]
            [hiccup.element :refer [link-to]]))

(defn login-form
  [anti-forgery-token & {:keys [next-url error-msg]}]
  (let [login-url (if (empty? next-url)
                    "/login"
                    (str "/login?next=" next-url))]
    [:div {:class "well well-width"}
     (f/form-to {:role "form"}
                [:post login-url]
                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "username" "Username")
                 (f/text-field {:class "form-control" :placeholder "Username"
                                :required ""} "username")]
                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "password" "Password")
                 (f/password-field {:class "form-control" :placeholder "Password"
                                    :required ""} "password")]
                [:div {:class "form-group"}
                 (f/submit-button {:class "btn btn-primary"} "Login")]
                (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))]))

(defn signup-form
  [anti-forgery-token]
  [:div {:class "well well-width"}
   (f/form-to {:role "form"}
              [:post "/signup"]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "email" "Email")
               (f/email-field {:class "form-control" :placeholder "Email"
                               :required ""} "email")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "username" "Username")
               (f/text-field {:class "form-control" :placeholder "Username"
                              :required ""} "username")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "password" "Password")
               (f/password-field {:class "form-control" :placeholder "Password"
                                  :required ""} "password")]
              [:div {:class "form-group"}
               (f/submit-button {:class "btn btn-primary"} "Signup")]
              (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))])
