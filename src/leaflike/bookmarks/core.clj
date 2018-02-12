(ns leaflike.bookmarks.core
  (:require [leaflike.bookmarks.validator :refer [valid-bookmark?
                                                  id?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.user.db :as user-db]
            [leaflike.user.auth :refer [throw-unauthorized]]
            [leaflike.utils :as utils]
            [clojure.walk :as walk]
            [clojure.string :as string]))

(defn- get-user
  [request]
  (let [username (:username request)]
    (first (user-db/get-member-auth-data username :id))))

(defn create
  [{:keys [params] :as request}]
  (let [bookmark (-> (select-keys (walk/keywordize-keys params)
                                  [:title :url :tags])
                     (update-in [:tags] #(if (empty? %)
                                           nil
                                           (string/split % #","))))
        user    (get-user request)]
    (if (valid-bookmark? bookmark)
      (let [bookmark (assoc bookmark
                            :member_id (:id user)
                            :created_at (utils/get-timestamp))]
        (bm-db/create bookmark))
      (throw (ex-info "Invalid params" {:bookmark bookmark})))))

(defn list-all
  [request]
  (bm-db/list-all {:member_id (:id (get-user request))}))

(defn fetch-bookmarks
  [{:keys [params] :as request}]
  (let [items-per-page 10
        page (Integer/parseInt (:page params))
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
  [{:keys [route-params] :as request}]
  (let [id        (:id route-params)
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (id? id)
      (bm-db/list-by-id params)
      (throw (ex-info "Invalid id" {:id id})))))

(defn delete
  [{:keys [route-params] :as request}]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)]
    (if (id? id)
      (bm-db/delete {:id id
                     :member_id (:id user)})
      (throw (ex-info "Invalid id" {:id id})))))
