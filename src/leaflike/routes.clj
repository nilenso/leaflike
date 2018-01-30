(ns leaflike.routes
  (:require [leaflike.middlewares :refer [with-auth-middlewares]]
            [ring.util.response :as res]
            [leaflike.layout :as layout]))

;; Home page controller (ring handler)
(defn home
  [request]
  (let [homepage (layout/application "Leaflike" (layout/index))]
    (-> (res/response homepage)
        (assoc :headers {"Content-Type" "text/html"}))))

(def home-routes
  {""  {:get home}})
