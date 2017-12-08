(ns leaflike.bookmarks.routes
  (:require [leaflike.bookmarks.db :as bmdb]))

(defn create-bookmark
  [request]
  (bmdb/create-bookmark request))

(defn list-bookmark
  [request]
  (bmdb/list-bookmark request))

(defn delete-bookmark
  [request]
  (bmdb/delete-bookmark request))

(def bookmarks-routes
  {"create-bookmark"         {:post create-bookmark}
   "list-bookmark"           {:get list-bookmark}
   ["delete-bookmark/" :id]  {:post delete-bookmark}})
