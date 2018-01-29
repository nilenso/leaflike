(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [ring.util.response :as res]))

(defn throw-unauthorized
  [status]
  (-> (res/redirect "/login")
      (assoc :status status)))

(defn wrap-authorized
  [handler]
  (fn [request]
    (if (nil? (get-in request [:session :username]))
      (throw-unauthorized 401)
      (handler request))))

(defn wrap-unauthorized
  [handler]
  (fn [request]
    (let [response (handler request)]
      (when (get-in request [:session :username])
        (throw-unauthorized 403))
      response)))

(defn logout-auth
  [request]
  (if-not (nil? (get-in request [:session :username]))
    (assoc-in (res/redirect "/login")
              [:session :username] nil)
    (throw-unauthorized 401)))

(defn login-auth
  [request member]
  (let [verify-password (:verify-password member)
        user-password   (get-in member [:auth-data :password])
        username        (get-in member [:auth-data :username])]

    (if (hashers/check verify-password user-password)
      ;; login
      (assoc (res/response {:status 200})
             :session {:username username})
      ;; 401
      (throw-unauthorized 401))))

(defn signup-auth
  [request username]
  (let [next-url        (get-in request [:query-params :next] "/")]
    (assoc (res/redirect next-url)
           :session {:username username})))
