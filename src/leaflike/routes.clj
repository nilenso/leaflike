(ns leaflike.routes
  (:require [leaflike.middlewares :refer [with-home-middlewares
                                          with-auth-middlewares]]
            [ring.util.response :as res]
            [leaflike.layout :as layout]))

#_(defn welcome
  [request]
  (res/response {:message "Welcome to Leaflike"}))

;; Home page controller (ring handler)
(defn home
  [request]
  (let [homepage (layout/application "Leaflike" (layout/index))]
    (-> (res/response homepage)
        (assoc :headers {"Content-Type" "text/html"}))))

(def home-routes
  {"" (with-home-middlewares {:get home})})
