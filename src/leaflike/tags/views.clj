(ns leaflike.tags.views)

(defn list-all
  [tags]
  [:div {:id "content"}
   [:div.row
    [:div.col
     (for [tag tags]
       [:a {:href (format "/bookmarks/tag/%s/page/1" (:name tag))}
        [:button.btn.btn-outline-primary.btn-sm (:name tag)]])]]])
