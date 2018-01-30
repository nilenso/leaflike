(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [ring.util.response :as res]))

(defn throw-unauthorized
  [status]
  (-> (res/redirect "/login")
      (assoc :status status)))

(defn get-username
  [request]
  (get-in request [:session :username]))

(defn wrap-authorized
  [handler]
  (fn [request]
    (if (nil? (get-username request))
      (throw-unauthorized 401)
      (handler request))))

(defn wrap-unauthorized
  [handler]
  (fn [request]
    (let [response (handler request)]
      (when (get-username request)
        (throw-unauthorized 403))
      response)))

(defn logout-auth
  [request]
  (if-not (nil? (get-username request))
    (-> res/redirect "/login"
        (assoc :session {:username nil}))
    (throw-unauthorized 401)))

(defn login-auth
  [request member]
  (let [verify-password (:verify-password member)
        user-password   (get-in member [:auth-data :password])
        username        (get-in member [:auth-data :username])
        next-url        (get-in request [:query-params :next] "/")]

    (if (hashers/check verify-password user-password)
      ;; login
      (-> res/redirect next-url
          (assoc :session {:username username}))
      ;; 401
      (throw-unauthorized 401))))

(defn signup-auth
  [request username]
  (let [next-url (get-in request [:query-params :next] "/")]
    (-> (res/redirect next-url)
        (assoc :session {:username username}))))
