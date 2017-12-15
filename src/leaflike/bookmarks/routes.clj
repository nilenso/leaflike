(ns leaflike.bookmarks.routes
  (:require [leaflike.bookmarks.core :as bm-core]))

(defn create
  [request]
  (bm-core/create request))

(defn list-bookmark
  [request]
  (bm-core/list-bookmark request))

(defn delete
  [request]
  (bm-core/delete request))

(def bookmarks-routes
  {"create-bookmark"         {:post create}
   "list-bookmark"           {:get list}
   ["delete-bookmark/" :id]  {:post delete}})
