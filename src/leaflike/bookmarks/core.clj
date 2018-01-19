(ns leaflike.bookmarks.core
  (:require [leaflike.bookmarks.validator :refer [valid-bookmark?
                                                  id?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.user.db :as user-db]
            [leaflike.user.auth :refer [user-session
                                        throw-unauthorized]]
            [leaflike.utils :as utils]))

(defn- get-user
  []
  (if-not (nil? @user-session)
    (let [identifier (get @user-session :identity)]
      (first (user-db/get-member-auth-data identifier :id)))
    (throw-unauthorized)))

(defn create
  [request]
  (let [body    (:body request)
        user    (get-user)]
    (if (valid-bookmark? body)
      (let [bookmark (assoc body :member_id (:id user)
                                 :created_at (utils/get-timestamp))]

        {:result (bm-db/create bookmark)
         :error  false})
      {:error  true
       :result "Invalid params"})))

(defn list-all
  [request]
  (let [params {:member_id (:id (get-user))}]
    {:result  (bm-db/list-all params)
     :error   false}))

(defn list-by-id
  [request]
  (let [id        (get-in request [:route-params :id])
        user      (get-user)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      {:result  (bm-db/list-by-id params)
       :error   false}
      {:error  true
       :result "Invalid params"})))

(defn delete
  [request]
  (let [id        (get-in request [:route-params :id])
        user      (get-user)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      {:result (bm-db/delete params)
       :error  false}
      {:error  true
       :result "Invalid params"})))
