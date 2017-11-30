(ns leaflike.server
     (:require [org.httpkit.server :as httpkit]
               [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
               [leaflike.routes :as routes]))

#_(defn app []
  (wrap-json-body routes/handler {:keywords? true :bigdecimals? true}))

(def app
   (-> routes/handler
       (wrap-json-body {:keywords? true :bigdecimals? true})
       (wrap-json-response)))

(defonce server (atom nil))

(defn start! []
  (reset! server (httpkit/run-server
                  app {:port 8080})))

(defn stop! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))
