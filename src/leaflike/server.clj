(ns leaflike.server
  (:require [bidi.ring :as bidi]
            [org.httpkit.server :as httpkit]
            [leaflike.config :refer [server-spec]]
            [leaflike.routes :refer [home-routes]]
            [leaflike.bookmarks.routes :refer [bookmarks-routes]]
            [leaflike.user.routes :refer [user-routes]]))

(def app-handler
  (bidi/make-handler ["/" (merge home-routes
                                 user-routes
                                 bookmarks-routes)]))

(defonce server (atom nil))

(defn start!
  []
  (let [server-spec (server-spec)]
    (reset! server (httpkit/run-server
                    app-handler server-spec))
    (println "Server started at : " (:ip server-spec) ":" (:port server-spec))))

(defn stop! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server []
  (stop!)
  (start!))
