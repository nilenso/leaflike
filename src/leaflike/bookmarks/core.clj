(ns leaflike.bookmarks.core
  (:require [leaflike.bookmarks.validator :refer [valid-bookmark?
                                                  id?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.user.db :as user-db]
            [leaflike.utils :as utils]))

(defn- get-user
  [session]
  (let [identifier (get session :identity)]
    (first (user-db/get-member-auth-data identifier :id))))

(defn create
  [request]
  (let [body    (-> request :body)
        session (-> request :session)
        user    (get-user session)]
    (if (valid-bookmark? body)
      (let [bookmark (assoc body :member_id (:id user)
                                 :created_at (utils/get-timestamp))]
        (bm-db/create bookmark))
      {:error "Invalid params"})))

(defn list-all
  [request]
  (let [session (-> request :session)
        user    (get-user session)]
    (bm-db/list-all (:id user))))

(defn list-by-id
  [request]
  (let [id        (-> request :route-params :id)
        session   (-> request :session)
        user      (get-user session)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      (bm-db/list-by-id params)
      {:error "Invalid params"})))

(defn delete
  [request]
  (let [id        (-> request :route-params :id)
        session   (-> request :session)
        user      (get-user session)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      (bm-db/delete params)
      {:error "Invalid params"})))
