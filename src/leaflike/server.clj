(ns leaflike.server
  (require [org.httpkit.server :as httpkit]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    "Welcome to Leaflike"})

(defonce server (atom nil))

(defn start-server []
  (reset! server (httpkit/run-server #'app {:port 8080})))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))
