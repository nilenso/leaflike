(ns leaflike.bookmarks.views
  (:require [hiccup.form :as f]))

(defn list-all
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

(defn add-bookmark
  [anti-forgery-token]
  [:div {:id "content"}
   [:div {:class "well"}
    (f/form-to {:role "form" :novalidate ""}
               [:post "/bookmarks"]
               [:div {:class "form-group"}
                (f/label {:class "control-label"} "url" "URL")
                (f/text-field {:class "form-control" :placeholder "URL"} "url")]
               [:div {:class "form-group"}
                (f/label {:class "control-label"} "title" "Title")
                (f/text-field {:class "form-control" :placeholder "Title"} "title")]
               [:div {:class "form-group"}
                (f/label {:class "control-label"} "tags" "Tags")
                (f/text-field {:class "form-control" :placeholder "Tags"} "tags")]

               [:div {:class "form-group"}
                (f/submit-button {:class "btn btn-primary"} "Submit")]

               (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))]])
