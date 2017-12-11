(ns leaflike.server
  (:require [org.httpkit.server :as httpkit]
            [leaflike.routes :as routes]))

(def app
  (-> routes/handler))

(defonce server (atom nil))

(defn start! []
  (reset! server (httpkit/run-server
                  app {:port 8000})))

(defn stop! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server []
  (stop!)
  (start!))
