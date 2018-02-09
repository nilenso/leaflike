(ns leaflike.bookmarks.views
  (:require [hiccup.form :as f]))

(defn list-all
  [bookmarks num-pages]
  [:div {:id "content"}
   [:div {:class "row"}
    [:div {:class "col"}
     [:h3 "Bookmarks"]]
    [:div {:class "col"}
     [:a {:href "/bookmarks/add"}
      [:button {:class "btn btn-large btn-primary"} "Add bookmark"]]]]
   [:table {:class "table"}
    [:thead {:class "thead-dark"}
     [:tr
      [:th {:scope "col"} "Title"]
      [:th {:scope "col"} "Tags"]
      [:th {:scope "col"} "Date"]]]
    [:tbody
     (for [bookmark bookmarks]
       [:tr
        [:td [:a {:href (:url bookmark)} (:title bookmark)]]
        [:td (:tags bookmark)]
        [:td (:created_at bookmark)]])]]

   [:table {:class "table"}
    (for [i (range num-pages)]
      [:td {:scope "col"}
       [:a {:href (str "/bookmarks/page/" (inc i))} (inc i)]])]])

(defn add-bookmark
  [anti-forgery-token]
  [:div {:id "content"}
   [:h3 "Add bookmark"]
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
