(ns leaflike.bookmarks.routes
  (:require [leaflike.bookmarks.core :as bm-core]
            [leaflike.middlewares :refer [with-auth-middlewares]]
            [leaflike.layout :refer [application]]
            [ring.util.response :as res]
            [buddy.auth :refer [authenticated?]]
            [leaflike.bookmarks.views :as views]))

(defn create
  [request]
  (res/response (bm-core/create request)))

#_(defn list-all
  [request]
  (res/response (bm-core/list-all request)))

#_(defn delete
  [request]
  (res/response (bm-core/delete request)))

#_(defn detail
  [request]
  (res/response (bm-core/list-by-id request)))

(defn list-all-view
  [request]
  (let [bookmarks (:result (bm-core/list-all request))]
    (print bookmarks)
    (-> (res/response (application "Bookmarks" (views/list-all-view bookmarks)))
        (assoc :headers {"Content-Type" "text/html"}))))

(def bookmarks-routes
  {"bookmarks" (with-auth-middlewares {:get  list-all-view
                                       :post create})})
