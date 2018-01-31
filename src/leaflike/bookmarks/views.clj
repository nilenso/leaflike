(ns leaflike.bookmarks.views
  (:require [hiccup.core :refer [html]]))

(defn list-all-view
  [bookmarks]
  [:div {:id "content"}
   [:h3 "Bookmarks"]
   [:table {:class "table"}
    [:thead {:class "thead-dark"}
     [:tr
      [:th {:scope "col"} "Id"]
      [:th {:scope "col"} "Title"]
      [:th {:scope "col"} "Tags"]
      [:th {:scope "col"} "Date"]]]
    [:tbody
     (for [bookmark bookmarks]
       [:tr
        [:th {:scope "row"} (:id bookmark)]
        [:td [:a {:href (:url bookmark)} (:title bookmark)]]
        [:td (:tags bookmark)]
        [:td (:created_at bookmark)]])]]])
