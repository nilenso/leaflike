(ns leaflike.bookmarks.routes
  (:require [leaflike.bookmarks.core :as bm-core]
            [leaflike.middlewares :refer [with-auth-middlewares]]
            [leaflike.layout :refer [user-view]]
            [ring.util.response :as res]
            [buddy.auth :refer [authenticated?]]
            [leaflike.bookmarks.views :as views]
            [ring.middleware.anti-forgery :as anti-forgery]
            [clojure.string :as string]))

(defn create
  [request]
  (let [result (bm-core/create request)]
    (-> (res/redirect "/bookmarks")
        (assoc-in [:headers "Content-Type"] "text/html"))))

(defn- bookmarks-list-view
  "Common function to show list of bookmarks. `view-type` can be either of:

  `:all-bookmarks` to show an unfiltered list of the user's bookmarks
  `:tag-bookmarks` to show a list of user's bookmarks with a specific tag"
  [view-type {:keys [params] :as request}]
  (let [current-page (if (string/blank? (:page params))
                       1
                       (Integer/parseInt (:page params)))
        request (update-in request
                           [:params :page]
                           (constantly current-page))
        tag (:tag params)
        username (get-in request [:session :username])
        {:keys [bookmarks num-pages]} (bm-core/fetch-bookmarks request)
        page-title (case view-type
                     :all-bookmarks "Bookmarks"
                     :tag-bookmarks (str "Bookmarks with tag: " tag))
        page-route-template (case view-type
                              :all-bookmarks "/bookmarks/page/%d"
                              :tag-bookmarks (str (format "/bookmarks/tag/%s" tag)
                                                  "/page/%d"))]
    (-> (res/response (user-view page-title
                                 username
                                 (views/list-all bookmarks
                                                 num-pages
                                                 current-page
                                                 page-route-template)))
        (assoc :headers {"Content-Type" "text/html"}))))

(def all-bookmarks-view (partial bookmarks-list-view :all-bookmarks))
(def tag-bookmarks-view (partial bookmarks-list-view :tag-bookmarks))

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
  {"bookmarks" {"" (with-auth-middlewares {:get  all-bookmarks-view
                                           :post create})
                ["/page/" :page] (with-auth-middlewares
                                   {:get all-bookmarks-view})
                ["/tag/" :tag] (with-auth-middlewares
                                 {:get tag-bookmarks-view})
                ["/tag/" :tag "/page/" :page] (with-auth-middlewares
                                                {:get tag-bookmarks-view})

                "/add" (with-auth-middlewares {:get create-view})}})
