(ns leaflike.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]))

(defn application
  [title content & {:keys [username error-msg]}]
  (html5 [:head
          [:title title]
          (include-css "//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css")
          (include-css "/css/styles.css")
          [:body
           [:header {:class "navbar navbar-dark bg-light"}
            [:a.navbar-brand "Leaflike"]
            [:div {:class "navbar-nav-scroll"
                   :id "navbarSupportedContent"}
             (when username [:ul {:class "navbar-nav"}
                             [:li {:class "nav-item"}
                              (str "Logged in as " username)]
                             [:li {:class "nav-item"}
                              [:a {:href "/logout"} "Logout"]]])]]
           [:div.container
            [:div#content
             [:h3 {:class "text-success"} title]
             (when error-msg
               [:div {:class "alert alert-danger"} error-msg])
             content]]]]))

(defn user-view
  [title username content & {:keys [error-msg]}]
  (application title content
               :username username
               :error-msg error-msg))

(defn index
  []
  [:div {:class "container"}
   [:div {:id "content"}
    [:h1 {:class "text-success"} "Welcome to Leaflike"]
    [:a {:href "/signup"} [:button {:class "btn btn-primary btn-space"} "Signup"]]
    [:a {:href "/login"}  [:button {:class "btn btn-primary btn-space"} "Login"]]]])
