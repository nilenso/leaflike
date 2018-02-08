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
        {:result (bm-db/create bookmark)
         :error false})
      {:error true
       :result "Invalid params"})))

(defn list-all
  [request]
  (let [params {:member_id (:id (get-user request))}]
    {:result  (bm-db/list-all params)
     :error   false}))

(defn fetch-bookmarks
  [request]
  (let [items-per-page 10
        page (Integer/parseInt (get-in request [:params :page]))
        page (dec page)
        user (get-user request)]
    (if (>= page 0)
      (let [bookmarks (bm-db/fetch-bookmarks {:member_id (:id user)
                                              :limit items-per-page
                                              :offset (* items-per-page page)})
            num-bookmarks (-> (bm-db/count-bookmarks {:member_id (:id user)})
                              first
                              :count)]
        {:result {:bookmarks bookmarks
                  :num-pages (/ num-bookmarks items-per-page)}
         :error false})
      {:error true
       :result "Invalid page number"})))

(defn list-by-id
  [request]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      {:result  (bm-db/list-by-id params)
       :error   false}
      {:error  true
       :result "Invalid params"})))

(defn delete
  [request]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (id? params)
      {:result (bm-db/delete params)
       :error  false}
      {:error  true
       :result "Invalid params"})))
