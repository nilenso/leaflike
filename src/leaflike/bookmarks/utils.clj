(ns leaflike.bookmarks.utils)

(defn format-tag-page-uri
  "Return a path with `tag` and `page` formatted in."
  [& {:keys [tag page]}]
  (format "/bookmarks/tag/%s/page/%d" tag page))

(defn format-page-uri
  "Return a path with `page` formatted in."
  [& {:keys [tag page]}]
  (format "/bookmarks/page/%d" page))
