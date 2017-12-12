(ns leaflike.user.auth
  (:require [buddy.hashers :as hashers]
            [buddy.auth.backends.session :refer [session-backend]]
            [leaflike.user.db :refer [get-member-auth-data]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.response :as res]))

(defn- user-auth-data
  [identifier]
  (let [auth-data (get-member-auth-data identifier)]
    (when-not (nil? auth-data)
      {:user-data (-> auth-data
                      (assoc-in [:username] (str (:username auth-data)))
                      (assoc-in [:email]    (str (:email auth-data)))
                      (dissoc   :created_on)
                      (dissoc   :password))
       :password (:password auth-data)})))

(defn login-auth
  [request auth-data]
  (let [username    (:username auth-data)
        password    (:password auth-data)
        session     (:session request)
        auth-data   (user-auth-data username)]

    (if (and auth-data
             (hashers/check password (:password auth-data)))
      ;; success
      (let [next-url (get-in request [:query-params :next] "/")
            session-updated (assoc session :identity (keyword username))]
        (-> (res/redirect next-url)
            (assoc :session session-updated)))
      ;; redirect to login
      (let [login "login.html"]
        (-> (res/resource-response login {:root "public"})
            (assoc :headers {"Content-Type" "text/html"})
            (assoc :status 401))))))

(defn- unauthorized-handler
  [request auth-data]
  (cond
    ;;authenticated -> 403
    (authenticated? request)
    (-> (res/resource-response "error.html" {:root "public"})
        (assoc :headers {"Content-Type" "text/html"})
        (assoc :status 403))
    ;; redirect to login
    :else
    (let [cur-url (:uri request)]
      (res/redirect (format "/login.html?next=%s" cur-url)))))

(def session-auth-backend
  (session-backend {:authfn login-auth
                    :unauthorized-handler unauthorized-handler}))
