(ns leaflike.server
  (:require [bidi.ring :as bidi]
            [org.httpkit.server :as httpkit]
            [leaflike.config :refer [server-spec]]
            [leaflike.routes :refer [home-routes]]
            [leaflike.bookmarks.routes :refer [bookmarks-routes]]
            [leaflike.user.routes :refer [user-routes]]
            [leaflike.user.auth :refer [user-session]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.json :refer [wrap-json-params
                                          wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]))

(def app-handler
  (bidi/make-handler ["/" (merge home-routes
                                 user-routes
                                 bookmarks-routes)]))

(defn app
  []
  (-> app-handler
      (wrap-resource "public")
      (wrap-json-params {:keywords? true :bigdecimals? true})
      wrap-json-response
      wrap-params))

(defonce server (atom nil))

(defn start!
  []
  (let [server-spec (server-spec)]
    (reset! server (httpkit/run-server
                    (app) server-spec))
    (println "Server started at : " (:ip server-spec) ":" (:port server-spec))))

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
