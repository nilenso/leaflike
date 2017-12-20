(ns leaflike.bookmarks.routes
  (:require [leaflike.bookmarks.core :as bm-core]
            [leaflike.middlewares :refer [with-auth-middlewares]]
            [ring.util.response :as res]
            [buddy.auth :refer [authenticated?]]))

(defn create
  [request]
  (res/response (bm-core/create request)))

(defn list-all
  [request]
  (res/response (bm-core/list-all request)))

(defn delete
  [request]
  (res/response (bm-core/delete request)))

(defn detail
  [request]
  (res/response (bm-core/list-by-id request)))


(def bookmarks-routes
  {"bookmarks"        (with-auth-middlewares {:post   create
                                              :get    list-all})
   ["bookmarks/" :id] (with-auth-middlewares {:get    detail
                                              :delete delete})})
