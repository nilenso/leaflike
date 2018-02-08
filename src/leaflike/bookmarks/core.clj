(ns leaflike.bookmarks.core
  (:require [leaflike.bookmarks.validator :refer [valid-bookmark?
                                                  id?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.user.db :as user-db]
            [leaflike.user.auth :refer [throw-unauthorized]]
            [leaflike.utils :as utils]
            [clojure.walk :as walk]))

(defn- get-user
  [request]
  (if-let [username (get-in request [:session :username])]
    (first (user-db/get-member-auth-data username :id))
    (throw-unauthorized 401)))

(defn create
  [request]
  (let [body (select-keys (walk/keywordize-keys (:params request))
                          [:title :url :tags])
        user    (get-user request)]
    (if (valid-bookmark? body)
      (let [bookmark (assoc body
                            :member_id (:id user)
                            :created_at (utils/get-timestamp))]
        (bm-db/create bookmark))
      (throw (ex-info "Invalid params" {:bookmark body})))))

(defn list-all
  [request]
  (let [params {:member_id (:id (get-user request))}]
    (bm-db/list-all params)))

(defn fetch-bookmarks
  [request]
  (let [items-per-page 10
        page (Integer/parseInt (get-in request [:params :page]))
        user (get-user request)]
    (if (>= page 1)
      (let [page (dec page)
            bookmarks (bm-db/fetch-bookmarks {:member_id (:id user)
                                              :limit items-per-page
                                              :offset (* items-per-page page)})
            num-bookmarks (-> (bm-db/count-bookmarks {:member_id (:id user)})
                              first
                              :count)]
        {:bookmarks bookmarks
         :num-pages (/ num-bookmarks items-per-page)})
      (throw (ex-info "Invalid page number" {:page page})))))

(defn list-by-id
  [request]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      (bm-db/list-by-id params)
      (throw (ex-info "Invalid id" {:id id})))))

(defn delete
  [request]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      (bm-db/delete params)
      (throw (ex-info "Invalid id" {:id id})))))
