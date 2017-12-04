(ns leaflike.server
  (:require [org.httpkit.server :as httpkit]
            [ring.util.response :as res]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [leaflike.routes :as routes]))

(defn wrap-ring-response
  [handler]
  (fn [request]
    (let [response (handler request)]
      (res/response response))))

(def app
  (-> routes/handler
      (wrap-params)
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-ring-response)
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
