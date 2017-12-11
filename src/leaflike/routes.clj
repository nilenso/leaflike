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
  (res/response {:ping (-> request :route-params :ping)}))

(def home-routes
  {""               (with-home-middlewares {:get welcome})
   ["ping/" :ping]  (with-home-middlewares {:get ping})})

(def handler
  (bidi/make-handler ["/" (merge home-routes user-routes)]))
