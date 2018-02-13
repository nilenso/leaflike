(ns leaflike.bookmarks.views
  (:require [hiccup.form :as f]
            [clojure.string :as string]))

(defn list-all
  [bookmarks num-pages current-page]
  [:div {:id "content"}
   [:div {:class "row"}
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

   [:ul.pagination
    ;; "Previous" button
    (let [disabled-prev? (= current-page 1)
          disabled-class (if disabled-prev?
                           " disabled"
                           "")]
      [:li {:class (str "page-item" disabled-class)}
       [:a {:class "page-link"
            :href (str "/bookmarks/page/" (dec current-page))}
        "Previous"]])
    ;; List of pages
    (for [page-num (range 1 (inc num-pages))]
      (let [active? (= page-num current-page)
            li-class "page-item"
            li-class (if active?
                       (str li-class " active")
                       li-class)]
        [:li {:class li-class}
         [:a {:class "page-link"
              :href (str "/bookmarks/page/" page-num)} page-num]]))

    ;; "Next" button
    (let [disabled-next? (= current-page num-pages)
          disabled-class (if disabled-next?
                           " disabled"
                           "")]
      [:li {:class (str "page-item" disabled-class)}
       [:a {:class "page-link"
            :href (str "/bookmarks/page/" (inc current-page))}
        "Next"]])]])

(defn add-bookmark
  [anti-forgery-token]
  [:div {:class "well"}
   (f/form-to {:role "form"}
              [:post "/bookmarks"]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "url" "URL")
               (f/text-field {:class "form-control" :placeholder "URL"
                              :required ""} "url")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "title" "Title")
               (f/text-field {:class "form-control" :placeholder "Title"
                              :required ""} "title")]
              [:div {:class "form-group"}
               (f/label {:class "control-label"} "tags" "Tags")
               (f/text-field {:class "form-control" :placeholder "Tags"} "tags")]

              [:div {:class "form-group"}
               (f/submit-button {:class "btn btn-primary"} "Submit")]

              (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))])
