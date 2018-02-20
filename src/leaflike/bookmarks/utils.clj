(ns leaflike.bookmarks.utils
  (:require [clojure.string :as string]))

(defn format-tag-page-uri
  "Return a path with `tag` and `page` formatted in."
  [& {:keys [tag page]}]
  (format "/bookmarks/tag/%s/page/%d" tag page))

(defn format-page-uri
  "Return a path with `page` formatted in."
  [& {:keys [tag page]}]
  (format "/bookmarks/page/%d" page))

(defn format-search-uri
  [& {:keys [search-query page]}]
  (format "/bookmarks/search/page/%d?search_query=%s" page search-query))
