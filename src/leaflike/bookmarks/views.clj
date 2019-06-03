(ns leaflike.bookmarks.views
  (:require [hiccup.form :as f]))

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
           :href (path-format-fn (dec current-page))}
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
                 :href (path-format-fn page-num)} page-num]]))))

   ;; "Next" button
   (let [disabled-next? (= current-page num-pages)
         disabled-class (if disabled-next?
                          " disabled"
                          "")]
     [:li {:class (str "page-item" disabled-class)}
      [:a {:class "page-link"
           :href (path-format-fn (inc current-page))}
       "Next"]])])

(defn form-to-action [post-url tooltip icon anti-forgery-token]
  (f/form-to {:role "form" :style "display: inline;"}
             [:post post-url]
             [:button.btn.btn-outline-secondary.btn-sm
              {:type        "submit" :value "Archive" :style "border: none;"
               :data-toggle "tooltip" :title tooltip}
              icon]
             (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token")))

(defn list-all
  [anti-forgery-token bookmarks num-pages current-page path-format-fn]
  [:div {:id "content"}
   [:table {:class "table"}
    [:tbody
     (for [bookmark bookmarks]
       [:tr
        [:td
         [:div.container
          [:div.row
           [:div.col [:b [:a {:href        (:url bookmark)
                              :target      "_blank"
                              :bookmark_id (str (:id bookmark))} (:title bookmark)]]]
           [:div.col-4 {:style "min-width: 150px;"}
            [:div.well.pull-right
             [:a {:href        (str "/bookmarks/edit/" (:id bookmark)
                                    "?next=" (path-format-fn current-page))
                  :bookmark_id (str (:id bookmark))}
              [:button.btn.btn-outline-secondary.btn-sm
               {:style "border: none;" :data-toggle "tooltip" :title "Edit bookmark"}
               [:i.fa.fa-pencil]]]
             ; all of the url endpoint now accept `next` in the query string. This
             ; field used for coming back to same page after any of the action
             ; (edit, favorite, read, delete) is done (previously it used to go home page)
             (let [[mark-read? tooltip icon] (if (:read bookmark) ;form for submitting read mark
                                               [false "Add bookmark" [:i.fa.fa-plus]]
                                               [true "Mark read" [:i.fa.fa-check]])
                   post-url (str "/bookmarks/read/" (:id bookmark)
                                 "?read=" mark-read?   ; mark unread if previously read else mark read
                                 "&next=" (path-format-fn current-page))]
               (form-to-action post-url tooltip icon anti-forgery-token))
             (let [[mark-fav? tooltip icon] (if (:favorite bookmark) ;form for submitting favorite mark
                                              [false "Unfavorite" [:i.fa.fa-heart {:style "color:red;"}]]
                                              [true "Mark favorite" [:i.fa.fa-heart]])
                   post-url (str "/bookmarks/favorite/" (:id bookmark)
                                 "?favorite=" mark-fav? ; mark unfavorite if previously favorite else mark favorite
                                 "&next=" (path-format-fn current-page))]
               (form-to-action post-url tooltip icon anti-forgery-token))
             (let [post-url (str "/bookmarks/delete/" (:id bookmark) ;form for submitting delete action
                                 "?next=" (path-format-fn current-page))]
               (form-to-action post-url "Delete bookmark" [:i.fa.fa-trash] anti-forgery-token))]]]
          [:div.row
           ;rethink about having date in the output, what the use of date?
           ;[:div.col-3
           ; [:div.bookmark-card-date
           ;  (->> (:created_at bookmark)
           ;       time-coerce/from-sql-time
           ;       (time-format/unparse (time-format/formatter "dd MMM yyyy")))]]
           [:div.col
            [:div.well.pull-right (for [tag (:tags bookmark)]
                                    [:a.badge.btn-tag {:href (format "/bookmarks/tag/%s/page/1" tag)} tag])]]]]]])]]
   (when (> num-pages 1)
     (pagination num-pages current-page path-format-fn))])

(defn bookmark-form
  [anti-forgery-token form-post-url {:keys [id url title tags all-tags]
                                     :or {url ""
                                          title ""
                                          tags ""}}]
  (let [existing-tag? (set tags)]
    [:div {:class "well"}
     [:script "$(document).ready(function() {
    $('#tags-multi-select').select2({
       tags: true,
       tokenSeparators: [',', ' ']
      });
});"]
     (f/form-to {:role "form"}
                [:post form-post-url]
                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "url" "URL")
                 (f/text-field {:class "form-control" :placeholder "URL"
                                :value url
                                :required ""} "url")]
                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "title" "Title")
                 (f/text-field {:class "form-control" :placeholder "Title"
                                :value title
                                :required ""} "title")]

                [:div {:class "form-group"}
                 (f/label {:class "control-label"} "tags" "Tags")
                 [:select#tags-multi-select
                  {:name "tags" :multiple "multiple" :class "form-control"}

                  (for [tag all-tags]
                    [:option {:value tag
                              :selected (if (existing-tag? tag)
                                          "selected"
                                          nil)} tag])]]
                [:div {:class "form-group"}
                 (f/submit-button {:class "btn btn-primary"} "Submit")]

                (when id
                  (f/hidden-field {:value id} "id"))
                (f/hidden-field {:value anti-forgery-token} "__anti-forgery-token"))]))


(defn pocket-import-form
  [anti-forgery-token]
  [:form {:method "post"
          :action "/bookmarks/import"
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
