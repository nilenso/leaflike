(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [ring.util.response :as res]))

(defonce user-session (atom nil))

(defn- throw-unauthorized
  [status]
  (do (when-not (nil? @user-session)
        (reset! user-session nil))
      (-> (res/redirect "/login")
          (assoc :status status))))

(defn wrap-authorized
  [handler]
  (fn [request]
    (if (nil? @user-session)
      (throw-unauthorized 401)
      (handler request))))

(defn wrap-unauthorized
  [handler]
  (fn [request]
    (let [response (handler request)]
      (when (nil? @user-session)
        (throw-unauthorized 403))
      response)))

(defn logout-auth
  [request]
  (if-not (nil? @user-session)
    (do (reset! user-session nil)
        (res/redirect "/login"))
    (throw-unauthorized 401)))

(defn login-auth
  [request member]
  (let [session         @user-session
        verify-password (:verify-password member)
        user-password   (get-in member [:auth-data :password])
        username        (get-in member [:auth-data :username])]

    (if (hashers/check verify-password user-password)
      ;; login
      (let [session-updated (assoc session :identity (keyword username))]
        (do (reset! user-session session-updated)
            (res/response {:status 200})))
      ;; 401
      (throw-unauthorized 401))))

(defn signup-auth
  [request username]
  (let [session         @user-session
        next-url        (get-in request [:query-params :next] "/")
        session-updated (assoc session :identity (keyword username))]
    (do (reset! user-session session-updated)
        (res/redirect next-url))))
