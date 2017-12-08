(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [leaflike.bookmarks.routes :refer [bookmarks-routes]]
            [leaflike.user.routes :refer [user-routes]]))

(defn welcome
  [_]
  {:message "Welcome to Leaflike"})

(defn ping
  [request]
  {:ping (-> request :route-params :ping)})

(def home-routes  {""               {:get welcome}
                   ["ping/" :ping]  {:get ping}})

(def routes (merge home-routes
                   bookmarks-routes
                   user-routes))

(def handler
  (bidi/make-handler ["/" routes]))
