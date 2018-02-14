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

(defn list-bookmarks-view
  [request]
  (let [current-page (Integer/parseInt (get-in request [:params :page]))
        username (get-in request [:session :username])
        {:keys [bookmarks num-pages]} (bm-core/fetch-bookmarks request)]
    (-> (res/response (user-view "Bookmarks" username (views/list-all bookmarks
                                                                      num-pages
                                                                      current-page
                                                                      "/bookmarks/page/%d")))
        (assoc :headers {"Content-Type" "text/html"}))))

(defn tag-filtered-view
  [{:keys [params] :as request}]
  (let [current-page (Integer/parseInt (:page params))
        tag (:tag params)
        username (get-in request [:session :username])
        {:keys [bookmarks num-pages]} (bm-core/fetch-bookmarks request)]
    (-> (res/response (user-view (str "Bookmarks with tag: " tag)
                                 username
                                 (views/list-all bookmarks
                                                 num-pages
                                                 current-page
                                                 (str (format "/bookmarks/tag/%s" tag)
                                                      "/page/%d"))))
        (assoc :headers {"Content-Type" "text/html"}))))

(defn create-view
  [request]
  (let [username (get-in request [:session :username])
        error-msg (get-in request [:flash :error-msg])]
    (-> (res/response (user-view "Add Bookmark"
                                 username
                                 (views/add-bookmark
                                  anti-forgery/*anti-forgery-token*)
                                 :error-msg error-msg))
        (assoc-in [:headers "Content-Type"] "text/html"))))

(def bookmarks-routes
  {"bookmarks" {"" (with-auth-middlewares {:get  list-all-view
                                           :post create})
                ["/tag/" :tag "/page/" :page] (with-auth-middlewares
                                                {:get tag-filtered-view})
                ["/page/" :page] (with-auth-middlewares
                                   {:get list-bookmarks-view})
                "/add" (with-auth-middlewares {:get create-view})}})
