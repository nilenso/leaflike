(ns leaflike.routes
  (:require [leaflike.middlewares :refer [with-auth-middlewares]]
            [ring.util.response :as res]
            [leaflike.layout :as layout]))

(defn welcome
  [request]
  (let [username (get-in request [:session :username])
        welcome-message (str "Welcome to Leaflike"
                             (when username
                               (str ", " username)))]
    (res/response {:message welcome-message})))

;; Home page controller (ring handler)
(defn home
  [request]
  (let [homepage (layout/application "Leaflike" (layout/index))]
    (-> (res/response homepage)
        (assoc :headers {"Content-Type" "text/html"}))))

(def home-routes
  {""        {:get home}
   "welcome" (with-auth-middlewares {:get welcome})})
