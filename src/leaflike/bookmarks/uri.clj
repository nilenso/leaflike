(ns leaflike.bookmarks.uri
  (:require [clojure.string :as string]))

(defn page
  "Return a path with `page` formatted in."
  [_ page-num]
  (format "/bookmarks/page/%d" page-num))

(defn read-page
  "Return a path with `page` formatted in."
  [_ page-num]
  (format "/bookmarks/readlist/page/%d" page-num))

(defn tag-page
  "Return a path with `tag` and `page` formatted in."
  [{:keys [tag] :as opts} page-num]
  (format "/bookmarks/tag/%s/page/%d" tag page-num))

(defn search
  [{:keys [search-query] :as opts} page-num]
  (format "/bookmarks/search/page/%d?search_query=%s" page-num search-query))
