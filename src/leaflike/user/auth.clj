(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [ring.util.response :as res]))

(defn redirect-to-login
  [next-page]
  (res/redirect (format "/login?next=%s" next-page)))

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
        (redirect-to-login (:uri request))))))

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
        next-url        (get-in request [:params :next] "/bookmarks")]
    (if (hashers/check verify-password user-password)
      ;; login
      (-> (res/redirect next-url)
          (assoc-in [:session :username] username))
      (assoc (res/redirect "/login")
             :flash {:error-msg "Invalid username/password"}))))

(defn signup-auth
  [request username]
  (let [next-url (get-in request [:query-params :next] "/bookmarks")]
    (-> (res/redirect next-url)
        (assoc-in [:session :username] username)
        (assoc :flash {:success-msg "Signup Successful"}))))
