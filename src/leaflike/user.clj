(ns leaflike.user
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.spec :as user-spec]
            [leaflike.user.views :as views]
            [ring.util.response :as res]
            [leaflike.layout :as layout]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.util.response :as res]))

(defn logged-in?
  [{:keys [session] :as request}]
  (:username session))

(defn signup
  [{:keys [params] :as request}]
  (let [user (select-keys params [:email :username :password])]
    (if (user-spec/valid-signup-details? params)
      (do (user-db/create-user user)
          (auth/signup-auth request (:username user)))
      (assoc (res/redirect "/signup")
             :flash {:error-msg "Invalid signup details"}))))

(defn login
  [{:keys [params] :as request}]
  (let [login-details   (select-keys params [:username :password])
        member (user-spec/valid-login-details? params)]
    (if member
      (let [data {:auth-data (select-keys member [:username :password :verify-password])
                  :verify-password  (:password login-details)}]
        (auth/login-auth request data))
      (assoc (res/redirect "/login")
             :flash {:error-msg "Invalid username/password"}))))

(defn logout
  [request]  
  (auth/logout-auth request))

(defn login-page
  [{{:keys [next]} :params :as request}]
  (if (logged-in? request)
    (res/redirect "/bookmarks")
    (let [error-msg (get-in request [:flash :error-msg])]
      (-> (layout/application "Login"
                              (views/login-form anti-forgery/*anti-forgery-token*
                                                :next-url next)
                              :error-msg error-msg)
          res/response
          (assoc :headers {"Content-Type" "text/html"}
                 :status 200)))))

(defn signup-page
  [request]
  (if (logged-in? request)
    (res/redirect "/bookmarks")
    (let [error-msg (get-in request [:flash :error-msg])]
      (-> (res/response (layout/application
                         "Signup"
                         (views/signup-form anti-forgery/*anti-forgery-token*)
                         :error-msg error-msg))
          (assoc :headers {"Content-Type" "text/html"}
                 :status 200)))))
