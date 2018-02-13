(ns leaflike.bookmarks.routes
  (:require [leaflike.bookmarks.core :as bm-core]
            [leaflike.middlewares :refer [with-auth-middlewares]]
            [leaflike.layout :refer [user-view]]
            [ring.util.response :as res]
            [buddy.auth :refer [authenticated?]]
            [leaflike.bookmarks.views :as views]
            [ring.middleware.anti-forgery :as anti-forgery]))

(defn create
  [request]
  (let [result (bm-core/create request)]
    (-> (res/redirect "/bookmarks")
        (assoc-in [:headers "Content-Type"] "text/html"))))

(defn list-all-view
  [request]
  (res/redirect "/bookmarks/page/1"))

;;; TODO: convert all underscore's to hyphens
(defn list-bookmarks-view
  [request]
  (let [current-page (Integer/parseInt (get-in request [:params :page]))
        username (get-in request [:session :username])
        {:keys [bookmarks num-pages]} (bm-core/fetch-bookmarks request)]
    (-> (res/response (user-view "Bookmarks" username (views/list-all bookmarks
                                                                      num-pages
                                                                      current-page)))
        (assoc :headers {"Content-Type" "text/html"}))))

(defn create-view
  [request]
  (let [username (get-in request [:session :username])]
    (-> (res/response (user-view "Add Bookmark"
                                 username
                                 (views/add-bookmark
                                  anti-forgery/*anti-forgery-token*)))
        (assoc-in [:headers "Content-Type"] "text/html"))))

(def bookmarks-routes
  {"bookmarks" {"" (with-auth-middlewares {:get  list-all-view
                                           :post create})
                ["/page/" :page] (with-auth-middlewares
                                   {:get list-bookmarks-view})
                "/add" (with-auth-middlewares {:get create-view})}})
