(ns leaflike.server
  (:require [leaflike.config                :refer [server-spec]]
            [leaflike.routes                :refer [app-routes]]
            [leaflike.middlewares :as middlewares]
            [bidi.ring                      :as    bidi-ring]
            [org.httpkit.server             :as    httpkit]
            [ring.middleware.resource       :refer [wrap-resource]]
            [ring.middleware.json           :refer [wrap-json-params
                                                    wrap-json-response]]
            [ring.middleware.params         :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.session        :refer [wrap-session]]
            [ring.middleware.session.memory :as    mem]
            [ring.middleware.anti-forgery   :refer [wrap-anti-forgery]]
            [clojure.tools.logging :as log]))

(defonce ^:private all-sessions (mem/memory-store))

(def app-handler (bidi-ring/make-handler app-routes))

(defn app
  []
  (-> app-handler
      middlewares/wrap-exception-handling
      wrap-anti-forgery
      (wrap-resource "public")
      (wrap-json-params {:keywords? true :bigdecimals? true})
      wrap-json-response
      middlewares/wrap-kebab-case
      wrap-keyword-params
      wrap-params
      wrap-flash
      (wrap-session {:store all-sessions})))

(defonce server (atom nil))

(defn start!
  []
  (let [server-spec (server-spec)]
    (reset! server (httpkit/run-server
                    (app) server-spec))
    (log/info (format "Server started at: %s:%d" (:ip server-spec) (:port server-spec)))))

(defn stop!
  []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server
  []
  (stop!)
  (start!))
