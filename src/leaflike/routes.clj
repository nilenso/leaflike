(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [leaflike.bookmarks.routes :refer [bookmarks-routes]]
            [leaflike.user.routes :refer [user-routes]]
            [leaflike.middlewares :refer [with-home-middlewares]]
            [ring.util.response :as res]))

(defn welcome
  [request]
  (res/response {:message "Welcome to Leaflike"}))

(defn ping
  [request]
  (prn request)
  (res/response {:ping (-> request :route-params :ping)}))

;; Login page controller
;; It returns a login page on get requests
(defn login
  [request]
  (res/content-type (res/resource-response "login.html" {:root "public"}) "text/html"))

(def routes  {""               (with-home-middlewares {:get welcome})
              ["ping/" :ping]  (with-home-middlewares {:get ping})
              "login"          (with-home-middlewares {:get login})})

(def home-handler
  (bidi/make-handler ["/" routes]))

(def bookmark-handler
  (bidi/make-handler ["/" bookmarks-routes]))
