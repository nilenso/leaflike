(ns leaflike.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]))

(defn application
  [title & content]
  (html5 [:head
          [:title title]
          (include-css "//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css")
          (include-css "/css/styles.css")
          [:body content]]))

(defn user-view
  [title username & content]
  (application title
               [:header {:class "navbar navbar-dark bg-light"}
                [:a.navbar-brand "Leaflike"]
                [:div {:class "navbar-nav-scroll"
                       :id "navbarSupportedContent"}
                 [:ul {:class "navbar-nav"}
                  [:li {:class "nav-item"}
                   (str "Logged in as " username)]
                  [:li {:class "nav-item"}
                   [:a {:href "/logout"} "Logout"]]]]]
               [:div {:class "container"} content]))

(defn index
  []
  [:div {:class "container"}
   [:div {:id "content"}
    [:h1 {:class "text-success"} "Welcome to Leaflike"]
    [:a {:href "/signup"} [:button {:class "btn btn-primary btn-space"} "Signup"]]
    [:a {:href "/login"}  [:button {:class "btn btn-primary btn-space"} "Login"]]]])
