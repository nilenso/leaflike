(ns leaflike.bookmarks.views
  (:require [hiccup.form :as f]
            [clojure.string :as string]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-format]))

(defn pagination
  [num-pages current-page path-format-fn]
  [:ul.pagination
   ;; "Previous" button
   (let [disabled-prev? (= current-page 1)
         disabled-class (if disabled-prev?
                          " disabled"
                          "")]
     [:li {:class (str "page-item" disabled-class)}
      [:a {:class "page-link"
           :href (path-format-fn :page (dec current-page))}
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
             :href (path-format-fn :page page-num)} page-num]]))

   ;; "Next" button
   (let [disabled-next? (= current-page num-pages)
         disabled-class (if disabled-next?
                          " disabled"
                          "")]
     [:li {:class (str "page-item" disabled-class)}
      [:a {:class "page-link"
           :href (path-format-fn :page (inc current-page))}
       "Next"]])])

(defn list-all
  [bookmarks num-pages current-page path-format-fn]
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
        [:td (for [tag (:tags bookmark)]
               [:a {:href (format "/bookmarks/tag/%s/page/1" tag)}
                [:button.btn.btn-outline-primary.btn-sm tag]])]
        [:td (->> (:created_at bookmark)
                  time-coerce/from-sql-time
                  (time-format/unparse (time-format/formatter :date)))]])]]
   (when (> num-pages 1)
     (pagination num-pages current-page path-format-fn))])

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
