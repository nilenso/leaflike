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

(defn wrap-authorization
  ;; checks if the incoming request is authorized
  [handler]
  ;; handle request
  (fn [request]
    (let [username (get-username request)]
      (if username
        (handler request)
        (throw-unauthorized 401)))))

(defn wrap-auth-response
  ;; if the request is authorized,
  ;; embed session in the response
  [handler]
  (fn [request]
    (let [response (handler request)
          username (get-username request)]
      (if username
        (-> response
            (assoc :session {:username username}))
        response))))

(defn logout-auth
  [request]
  (if-not (nil? (get-username request))
    (-> (res/redirect "/login")
        (assoc :session {}))
    (throw-unauthorized 401)))

(defn login-auth
  [request member]
  (let [verify-password (:verify-password member)
        user-password   (get-in member [:auth-data :password])
        username        (get-in member [:auth-data :username])
        next-url        (get-in request [:query-params :next] "/")]

    (if (hashers/check verify-password user-password)
      ;; login
      (-> (res/redirect next-url)
          (assoc :session {:username username}))
      ;; 401
      (throw-unauthorized 401))))

(defn signup-auth
  [request username]
  (let [next-url (get-in request [:query-params :next] "/")]
    (-> (res/redirect next-url)
        (assoc :session {:username username}))))
