(ns leaflike.bookmarks.core
  (:require [leaflike.bookmarks.validator :refer [valid-bookmark?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.user.db :as user-db]
            [leaflike.user.auth :refer [throw-unauthorized]]
            [leaflike.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(defn- get-user
  [request]
  (let [username (:username request)]
    (first (user-db/get-member-auth-data username :id))))

(defn- format-tags
  "Convert :tags in a bookmark from a comma-separated string to a vector
  of strings."
  [bookmark]
  (update bookmark :tags
          #(if (string/blank? %)
             nil
             (string/split % #","))))

(defn create
  [{:keys [params] :as request}]
  (let [bookmark (-> (select-keys params
                                  [:title :url :tags])
                     format-tags)
        user    (get-user request)]
    (if (valid-bookmark? bookmark)
      (let [bookmark (assoc bookmark
                            :member_id (:id user)
                            :created_at (utils/get-timestamp))]
        (bm-db/create bookmark))
      (throw (ex-info "Invalid params" {:type :invalid-bookmark
                                        :data bookmark})))))

(defn list-all
  [request]
  (bm-db/list-all {:member_id (:id (get-user request))}))

(let [;; default number of pages when paginating
      items-per-page 10]
  (defn fetch-bookmarks
    [{:keys [params] :as request}]
    (let [page (:page params)
          user (get-user request)
          tag (:tag params)]
      (if (>= page 1)
        (let [page (dec page)
              query {:member_id (:id user)
                     :tag tag}
              bookmarks (bm-db/fetch-bookmarks (merge query
                                                      {:limit items-per-page
                                                       :offset (* items-per-page page)}))
              num-bookmarks (-> (bm-db/count-bookmarks query)
                                first
                                :count)]
          {:bookmarks bookmarks
           :num-pages (int (Math/ceil (/ num-bookmarks items-per-page)))})
        (throw (ex-info "Invalid page number" {:type :invalid-page
                                               :data page}))))))

(defn list-by-id
  [{:keys [route-params] :as request}]
  (let [id        (:id route-params)
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (s/valid? :leaflike.bookmarks.validator/id id)
      (bm-db/list-by-id params)
      (throw (ex-info "Invalid id" {:type :invalid-id
                                    :data id})))))

(defn delete
  [{:keys [route-params] :as request}]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)]
    (if (s/valid? :leaflike.bookmarks.validator/id id)
      (bm-db/delete {:id id
                     :member_id (:id user)})
      (throw (ex-info "Invalid id" {:type :invalid-id
                                    :id id})))))
