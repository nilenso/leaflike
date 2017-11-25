(ns leaflike.core
  (require [org.httpkit.server :as httpkit]
           [leaflike.migrations :refer [migrate-db rollback-db]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn setup []
  (migrate-db)
  (reset! server (httpkit/run-server #'app {:port 8080})))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    "Welcome to Leaflike"})

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (setup))
