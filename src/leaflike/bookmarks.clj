(ns leaflike.bookmarks
  (:require [leaflike.bookmarks.spec :refer [valid-bookmark?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.bookmarks.utils :refer [format-tag-page-uri
                                              format-page-uri]]
            [leaflike.user.db :as user-db]
            [leaflike.user.auth :refer [throw-unauthorized]]
            [leaflike.utils :as utils]
            [leaflike.layout :refer [user-view]]
            [leaflike.bookmarks.views :as views]
            [ring.middleware.anti-forgery :as anti-forgery]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [ring.util.response :as res]))

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
        (bm-db/create bookmark)
        (-> (res/redirect "/bookmarks")
            (assoc-in [:headers "Content-Type"] "text/html")))
      (assoc (res/redirect "/bookmarks/add")
             :flash {:error-msg "Invalid bookmark"}))))

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
        (assoc (res/redirect "/bookmarks")
               :flash {:error-msg "Invalid page number"})))))

(defn list-by-id
  [{:keys [route-params] :as request}]
  (let [id        (:id route-params)
        user      (get-user request)
        params    {:id id :member_id (:id user)}]
    (if (s/valid? :leaflike.bookmarks.validator/id id)
      (bm-db/list-by-id params)
      (assoc (res/redirect "/bookmarks")
             :flash {:error-msg "Invalid bookmark id"}))))

(defn delete
  [{:keys [route-params] :as request}]
  (let [id        (get-in request [:route-params :id])
        user      (get-user request)]
    (if (s/valid? :leaflike.bookmarks.validator/id id)
      (bm-db/delete {:id id
                     :member_id (:id user)})
      (assoc (res/redirect "/bookmarks")
             :flash {:error-msg "Invalid bookmark id"}))))


(defn- bookmarks-list-view
  "Common function to show list of bookmarks. `view-type` can be either of:

  `:all-bookmarks` to show an unfiltered list of the user's bookmarks
  `:tag-bookmarks` to show a list of user's bookmarks with a specific tag"
  [view-type {:keys [params] :as request}]
  (let [current-page (if (string/blank? (:page params))
                       1
                       (Integer/parseInt (:page params)))
        request (update-in request
                           [:params :page]
                           (constantly current-page))
        tag (:tag params)
        username (get-in request [:session :username])
        {:keys [bookmarks num-pages]} (fetch-bookmarks request)
        page-title (case view-type
                     :all-bookmarks "Bookmarks"
                     :tag-bookmarks (str "Bookmarks with tag: " tag))
        path-format-fn (case view-type
                         :all-bookmarks format-page-uri
                         :tag-bookmarks (partial format-tag-page-uri :tag tag))]
    (-> (res/response (user-view page-title
                                 username
                                 (views/list-all bookmarks
                                                 num-pages
                                                 current-page
                                                 path-format-fn)))
        (assoc :headers {"Content-Type" "text/html"}))))

(def all-bookmarks-view (partial bookmarks-list-view :all-bookmarks))
(def tag-bookmarks-view (partial bookmarks-list-view :tag-bookmarks))

(defn create-view
  [request]
  (let [username (get-in request [:session :username])
        error-msg (get-in request [:flash :error-msg])]
    (-> (res/response (user-view "Add Bookmark"
                                 username
                                 (views/add-bookmark
                                  anti-forgery/*anti-forgery-token*)
                                 :error-msg error-msg))
        (assoc-in [:headers "Content-Type"] "text/html"))))
