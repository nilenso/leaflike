(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [buddy.auth.backends.session :refer [session-backend]]
            [ring.util.response :as res]
            [buddy.auth :refer [authenticated?
                                throw-unauthorized]]))

(defn login-auth
  [request member]
  (let [session         (:session request)
        verify-password (:verify-password member)
        user-password   (get-in member [:auth-data :password])
        username        (get-in member [:auth-data :username])]

    (if (and member
             (hashers/check verify-password user-password))
      ;; login
      (let [next-url        (get-in request [:query-params :next] "/")
            session-updated (assoc session :identity (keyword username))]
        (-> (res/redirect next-url)
            (assoc :session session-updated)))
      ;; 401
      (throw-unauthorized))))

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
