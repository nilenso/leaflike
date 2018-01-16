(ns leaflike.bookmarks.views
  (:require [hiccup.core :refer [html]]))

#_(defn bookmarks-homepage
  [])

(defn bookmark-list-view
  [data]
  (html
   [:ul
    (for [bookmark data]
      [:li bookmark])]))
