(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [buddy.auth.backends.session :refer [session-backend]]
            [ring.util.response :as res]
            [buddy.auth :refer [authenticated?]]))

#_(defn- user-auth-data
  [identifier]
  (let [auth-data (first (get-member-auth-data identifier))]
    (when-not (nil? auth-data)
      {:user-data (-> auth-data
                      (assoc-in [:username] (str (:username auth-data)))
                      (assoc-in [:email]    (str (:email auth-data)))
                      (dissoc   :created_on)
                      (dissoc   :password))
       :password (:password auth-data)})))

(defn signup-auth
  [request username]
  (let [session         (:session request)
        next-url        (get-in request [:query-params :next] "/")
        session-updated (assoc session :identity (keyword username))]
    (-> (res/redirect next-url)
        (assoc :session session-updated))))

(defn- unauthorized-handler
  [request auth-data]
  (cond
    ;;authenticated -> 403
    (authenticated? request)
    (-> (res/response {:msg "Unauthorized"})
        (assoc :headers {"Content-Type" "application/json"})
        (assoc :status 403))
    ;; redirect to login
    :else
    (let [cur-url (:uri request)]
      (res/redirect (format "/login.html?next=%s" cur-url)))))

(def session-auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))
