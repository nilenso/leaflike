(ns leaflike.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]))

(defn application
  [title & content]
  (html5 {:ng-app "leaflikeApp" :lang "en"}
         [:head
          [:title title]
          (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css")
          (include-css "/css/styles.css")
          [:body
           [:div {:class "container"} content]]]))

(defn index
  []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Welcome to Leaflike"]
   (link-to {:type "button" :class "btn btn-primary btn-lg btn-space"} "/login" "Login")
   (link-to {:type "button" :class "btn btn-secondary btn-lg btn-space"} "/signup" "Sign up")])
