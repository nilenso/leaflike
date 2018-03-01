(ns leaflike.routes
  (:require [leaflike.middlewares :refer [with-auth-middlewares]]
            [ring.util.response :as res]
            [leaflike.layout :as layout]
            [leaflike.user :as user]
            [leaflike.bookmarks :as bookmarks]
            [leaflike.tags :as tags]))

(defn welcome
  [request]
  (let [username (get-in request [:session :username])
        welcome-message (str "Welcome to Leaflike"
                             (when username
                               (str ", " username)))]
    (res/response {:message welcome-message})))

;; Home page controller (ring handler)
(defn home
  [request]
  (if (get-in request [:session :username])
    (res/redirect "/bookmarks")
    (let [homepage (layout/application "Leaflike" (layout/index))]
      (-> (res/response homepage)
          (assoc :headers {"Content-Type" "text/html"})))))

(defn user-routes
  []
  {;; existing user
   "login"        {:post user/login
                   :get  user/login-page}
   "logout"       (with-auth-middlewares  {:get user/logout})
   ;; new user
   "signup"       {:post user/signup
                   :get  user/signup-page}})

(defn bookmarks-routes
  []
  {"bookmarks" {"" (with-auth-middlewares {:get  bookmarks/all-bookmarks-view
                                           :post bookmarks/create})
                ["/page/" :page] (with-auth-middlewares
                                   {:get bookmarks/all-bookmarks-view})
                ["/tag/" :tag] (with-auth-middlewares
                                 {:get bookmarks/tag-bookmarks-view})
                ["/tag/" :tag "/page/" :page] (with-auth-middlewares
                                                {:get bookmarks/tag-bookmarks-view})
                ["/search/page/" :page] (with-auth-middlewares
                                          {:get bookmarks/search-bookmarks-view})
                "/add" (with-auth-middlewares {:get bookmarks/create-view})}})

(defn tags-routes
  []
  {"tags" {"" (with-auth-middlewares {:get tags/tags-list})}})

(defn home-routes
  []
  {""        {:get home}
   "welcome" (with-auth-middlewares {:get welcome})})

(defn app-routes
  []
  ["/" (merge (home-routes)
              (user-routes)
              (bookmarks-routes)
              (tags-routes))])
