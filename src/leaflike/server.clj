(ns leaflike.server
  (:require [bidi.ring :as bidi]
            [org.httpkit.server :as httpkit]
            [leaflike.config :refer [server-spec]]
            [leaflike.routes :refer [home-routes]]
            [leaflike.bookmarks.routes :refer [bookmarks-routes]]
            [leaflike.user.routes :refer [user-routes]]
            [leaflike.user.auth :refer [user-session]]))

(def handler
  (bidi/make-handler ["/" (merge home-routes
                                 user-routes
                                 bookmarks-routes)]))
;; todo
(def app
  (-> handler))

(defonce server (atom nil))

(defn start! []
  (reset! server (httpkit/run-server
                  app (server-spec))))

(defn stop! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)
    (reset! user-session nil)))

(defn restart-server []
  (stop!)
  (start!))
