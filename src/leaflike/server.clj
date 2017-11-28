(ns leaflike.server
  (:require [org.httpkit.server :as httpkit]
            [ring.middleware.json :refer [wrap-json-body]]
            [leaflike.routes :as routes]))

(defn app []
  (wrap-json-body routes/handler {:keywords? true :bigdecimals? true}))

(defonce server (atom nil))

(defn start! []
  (reset! server (httpkit/run-server
                  (app) {:port 8080})))

(defn stop! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))
