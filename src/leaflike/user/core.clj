(ns leaflike.user.core
  (:require [leaflike.user.db :as user-db]
            [leaflike.user.auth :as auth]
            [leaflike.user.validator :as validator]
            [leaflike.user.views :as views]
            [ring.util.response :as res]
            [leaflike.layout :as layout]
            [ring.middleware.anti-forgery :as anti-forgery]))

(defn signup
  [{:keys [params] :as request}]
  (let [user (select-keys params [:email :username :password])]
    (if (validator/valid-signup-details? user)
      (do (user-db/create-user user)
          (auth/signup-auth request (:username user)))
      (throw (ex-info "Invalid params" {:type :invalid-signup
                                        :data params})))))

(defn login
  [{:keys [params] :as request}]
  (let [login-details   (select-keys params [:username :password])
        member (validator/valid-login-details? login-details)]
    (if member
      (let [data {:auth-data (select-keys member [:username :password :verify-password])
                  :verify-password  (:password login-details)}]
        (auth/login-auth request data))
      (throw (ex-info "Invalid login credentials" {:type :invalid-login
                                                   :data params})))))

(defn logout
  [request]  
  (auth/logout-auth request))

(defn login-page
  [{{:keys [next]} :params :as request}]
  (let [error-msg (get-in request [:flash :error-msg])]
    (-> (layout/application "Login"
                            (views/login-form anti-forgery/*anti-forgery-token*
                                              :next-url next)
                            :error-msg error-msg)
        res/response
        (assoc :headers {"Content-Type" "text/html"}
               :status 200))))

(defn signup-page
  [request]
  (let [error-msg (get-in request [:flash :error-msg])]
    (-> (res/response (layout/application 
                       "Signup"
                       (views/signup-form anti-forgery/*anti-forgery-token*)
                       :error-msg error-msg))
        (assoc :headers {"Content-Type" "text/html"}
               :status 200))))
