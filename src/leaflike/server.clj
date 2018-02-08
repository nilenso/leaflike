(ns leaflike.server
  (:require [leaflike.config                :refer [server-spec]]
            [leaflike.routes                :refer [home-routes]]
            [leaflike.bookmarks.routes      :refer [bookmarks-routes]]
            [leaflike.user.routes           :refer [user-routes]]
            [bidi.ring                      :as    bidi]
            [org.httpkit.server             :as    httpkit]
            [ring.middleware.resource       :refer [wrap-resource]]
            [ring.middleware.json           :refer [wrap-json-params
                                                    wrap-json-response]]
            [ring.middleware.params         :refer [wrap-params]]
            [ring.middleware.session        :refer [wrap-session]]
            [ring.middleware.session.memory :as    mem]
            [ring.middleware.anti-forgery   :refer [wrap-anti-forgery]]
            [clojure.tools.logging :as log]))

(defonce ^:private all-sessions (mem/memory-store))

(def app-handler
  (bidi/make-handler ["/" (merge home-routes
                                 user-routes
                                 bookmarks-routes)]))

(defn app
  []
  (-> app-handler
      wrap-anti-forgery
      (wrap-resource "public")
      (wrap-json-params {:keywords? true :bigdecimals? true})
      wrap-json-response
      wrap-params
      (wrap-session {:store all-sessions})))

(defonce server (atom nil))

(defn start!
  []
  (let [server-spec (server-spec)]
    (reset! server (httpkit/run-server
                    (app) server-spec))
    (log/info (format "Server started at: %s:%d" (:ip server-spec) (:port server-spec)))))

(defn stop! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server []
  (stop!)
  (start!))
