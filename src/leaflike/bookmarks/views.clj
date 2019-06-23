(ns leaflike.bookmarks.views
  (:require [hiccup.form :as f]
            [clojure.string :as string]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-format]))

(defn truncated-page-list
  [num-pages current-page]
  (let [window-size (min 4 num-pages)
        half-window-size (int (/ window-size 2))
        first-window-max window-size
        last-window-min (- num-pages (dec window-size))

        first-window (range 1 (inc first-window-max))
        last-window (when (> last-window-min first-window-max)
                      (range last-window-min (inc num-pages)))
        middle-window (remove #(or (<= % first-window-max)
                                   (>= % last-window-min))
                              (map #(+ current-page %)
                                   (range (- half-window-size)
                                          (inc half-window-size))))
        windows (remove empty? [first-window middle-window last-window])]
    (reduce (fn [all-pages window]
              (concat all-pages
                      (if (> (- (first window) (last all-pages)) 1)
                        [:ellipsis]
                        [])
                      window))
            (first windows)
            (rest windows))))


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
           :href  (path-format-fn (dec current-page))}
       "Previous"]])

   ;; List of pages
   (let [visible-pages (truncated-page-list num-pages current-page)]
     (for [page-num visible-pages]
       (if (= page-num :ellipsis)
         [:li {:class "page-item disabled"} [:span.page-link "..."]]
         (let [active? (= page-num current-page)
               li-class "page-item"
               li-class (if active?
                          (str li-class " active")
                          li-class)]
           [:li {:class li-class}
            [:a {:class "page-link"
                 :href  (path-format-fn page-num)} page-num]]))))

   ;; "Next" button
   (let [disabled-next? (= current-page num-pages)
         disabled-class (if disabled-next?
                          " disabled"
                          "")]
     [:li {:class (str "page-item" disabled-class)}
      [:a {:class "page-link"
           :href  (path-format-fn (inc current-page))}
       "Next"]])])

(defn list-all
  [anti-forgery-token bookmarks num-pages current-page path-format-fn]
  [:div {:id "content"}
   [:div {:class "row"}
    [:div {:class "col"}
     [:a {:href "/bookmarks/add"}
      [:button {:class "btn btn-large btn-primary"} "Add bookmark"]]]]
   [:table {:class "table"}
    [:thead {:class "thead-dark"}
     [:tr
      [:th {:scope "col"} "Title"]
      [:th {:scope "col"} ""]
      [:th {:scope "col"} ""]
      [:th {:scope "col"} "Tags"]
      [:th {:scope "col"} "Date"]]]
    [:tbody
     (for [bookmark bookmarks]
       [:tr
        [:td [:a.col {:href        (:url bookmark)
                      :target      "_blank"
                      :bookmark_id (str (:id bookmark))} (:title bookmark)]]
        [:td [:a {:href        (str "/bookmarks/edit/" (:id bookmark))
                  :bookmark_id (str (:id bookmark))}
              [:button.btn.btn-sm.btn-outline-secondary "Edit"]]]
        [:td (f/form-to {:role "form"}
                        [:post (str "/bookmarks/delete/" (:id bookmark))]
                        (f/submit-button {:class "btn btn-sm btn-outline-secondary"} "Delete")
                        (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))]
        [:td (for [tag (:tags bookmark)]
               [:a {:href (format "/bookmarks/tag/%s/page/1" tag)}
                [:button.btn.btn-outline-primary.btn-sm tag]])]
        [:td (->> (:created_at bookmark)
                  time-coerce/from-sql-time
                  (time-format/unparse (time-format/formatter :date)))]])]]
   (when (> num-pages 1)
     (pagination num-pages current-page path-format-fn))])

(defn bookmark-form
  [anti-forgery-token form-post-url {:keys [id url title tags all-tags collaborators]
                                     :or   {url           ""
                                            title         ""
                                            tags          ""
                                            collaborators ""}}]
  (let [existing-tag? (set tags)]
    [:div {:class "well"}
     [:script "$(document).ready(function() {
    $('#tags-multi-select').select2({
       tags: true,
       tokenSeparators: [',', ' ']
      });
});"]
     [:script "$(document).ready(function() {
    $('#collaborators-multi-select').select2({
       tags: true,
       tokenSeparators: [',', ' ']
      });
});"]
     (f/form-to {:role "form"}
                [:post form-post-url]
                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "url" "URL")
                 (f/text-field {:class    "form-control" :placeholder "URL"
                                :value    url
                                :required ""} "url")]
                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "title" "Title")
                 (f/text-field {:class    "form-control" :placeholder "Title"
                                :value    title
                                :required ""} "title")]

                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "tags" "Tags")
                 [:select#tags-multi-select
                  {:name "tags" :multiple "multiple" :class "form-control"}

                  (for [tag all-tags]
                    [:option {:value    tag
                              :selected (if (existing-tag? tag)
                                          "selected"
                                          nil)} tag])]]

                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "collaborators" "Collaborators")
                 [:select#collaborators-multi-select
                  {:name "collaborators" :multiple "multiple" :class "form-control"}

                  (for [collaborator collaborators]
                    [:option {:value    collaborator
                              :selected "selected"} collaborator])]]

                [:div {:class "form-group"}
                 (f/submit-button {:class "btn btn-primary"} "Submit")]

                (when id
                  (f/hidden-field {:value id} "id"))
                (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))]))


(defn pocket-import-form
  [anti-forgery-token]
  [:form {:method  "post"
          :action  "/bookmarks/import"
          :enctype "multipart/form-data"}
   [:div
    [:label {:for "pocket_html"} "You can export your bookmarks from pocket from "
     [:a {:href "https://getpocket.com/export"} "here"]
     ". Choose exported html file and submit."]
    [:div
     [:input {:id "pocket_html", :name "pocket_html", :accept ".txt, .html", :multiple "", :type "file" :required ""}]
     [:input {:id "__anti-forgery-token", :name "__anti-forgery-token", :value anti-forgery-token, :type "hidden"}]]]

   [:div
    [:button.btn.btn-primary "Submit"]]])
