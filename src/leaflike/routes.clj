(ns leaflike.routes
  (:require [leaflike.middlewares :refer [with-home-middlewares
                                          with-auth-middlewares]]
            [ring.util.response :as res]))

(defn welcome
  [request]
  (res/response {:message "Welcome to Leaflike"}))

;; Home page controller (ring handler)
(defn home
  [request]
  (-> (res/resource-response "index.html" {:root "public"})
      (assoc :headers {"Content-Type" "text/html"})
      (assoc :status 200)))

(def home-routes
  {""               (with-home-middlewares {:get welcome})
   "home"           (with-auth-middlewares {:get home})})
