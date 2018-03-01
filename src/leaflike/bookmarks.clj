(ns leaflike.bookmarks
  (:require [leaflike.bookmarks.spec :refer [valid-bookmark?]]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.bookmarks.uri :as uri]
            [leaflike.tags.db :as tags-db]
            [leaflike.user.db :as user-db]
            [leaflike.user.auth :refer [throw-unauthorized]]
            [leaflike.utils :as utils]
            [leaflike.handler-utils :as hutils]
            [leaflike.layout :as layout]
            [leaflike.bookmarks.views :as views]
            [ring.middleware.anti-forgery :as anti-forgery]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [ring.util.response :as res]))

(defn- split-tags
  [tags]
  (if (string/blank? tags)
    nil
    (remove string/blank? (map string/trim (string/split tags #",")))))

(defn create
  [{:keys [params] :as request}]
  (if (valid-bookmark? params)
    (let [user (hutils/get-user request)
          bookmark (-> (select-keys params [:title :url])
                       (assoc :member_id (:id user)
                              :created_at (utils/get-timestamp)))
          tags (split-tags (:tags params))
          bookmark-id (bm-db/create bookmark)]
      (when (not-empty tags)
        (tags-db/create tags)
        (bm-db/tag-bookmark bookmark-id tags))
      (-> (res/redirect "/bookmarks")
          (assoc-in [:headers "Content-Type"] "text/html")))
    (assoc (res/redirect "/bookmarks/add")
           :flash {:error-msg "Invalid bookmark"})))

(defn edit
  [{:keys [params] :as request}]
  (if (valid-bookmark? params)
    (let [user (hutils/get-user request)
          bookmark-id (Integer/parseInt (:id params))
          updated-keys (select-keys params [:title :url])
          tags (split-tags (:tags params))]
      (bm-db/update-bookmark bookmark-id (:id user) updated-keys)
      (bm-db/remove-all-tags bookmark-id)
      (when (not-empty tags)
        (tags-db/create tags)
        (bm-db/tag-bookmark bookmark-id tags))
      (-> (res/redirect "/bookmarks")
          (assoc-in [:headers "Content-Type"] "text/html")))
    (assoc (res/redirect (str "/bookmarks/edit/" (:id params)))
           :flash {:error-msg "Invalid bookmark"})))

(def items-per-page 10)

(defn fetch-bookmarks
  [username {:keys [tag search-terms] :as params}]
  (let [user (hutils/get-user {:session {:username username}})
        page (dec (:page params))
        query {:member-id (:id user)
               :tag tag
               :search-terms search-terms}
        bookmarks (bm-db/fetch-bookmarks (merge query
                                                {:limit items-per-page
                                                 :offset (* items-per-page page)}))
        num-bookmarks (bm-db/count-bookmarks query)]
    {:bookmarks bookmarks
     :num-pages (int (Math/ceil (/ num-bookmarks items-per-page)))}))

(defn list-by-id
  [{:keys [route-params] :as request}]
  (let [id        (:id route-params)
        user      (hutils/get-user request)
        params    {:id id :member-id (:id user)}]
    (if (s/valid? :leaflike.bookmarks.spec/id id)
      (bm-db/list-by-id params)
      (assoc (res/redirect "/bookmarks")
             :flash {:error-msg "Invalid bookmark id"}))))

(defn delete
  [{:keys [route-params] :as request}]
  (let [user      (hutils/get-user request)
        unparsed-id (get-in request [:route-params :id])]
    (if (s/valid? :leaflike.bookmarks.spec/id unparsed-id)
      (let [id (Integer/parseInt unparsed-id)]
        (do (bm-db/remove-all-tags id)
            (bm-db/delete {:id id
                           :member-id (:id user)})))
      (assoc (res/redirect "/bookmarks")
             :flash {:error-msg "Invalid bookmark id"}))))

(defn- view-type-info
  [view-type {:keys [search-query tag] :as params}]
  (case view-type
    :all-bookmarks {:page-title "Bookmarks"
                    :path-format-fn (partial uri/page params)}
    :tag-bookmarks {:page-title (str "Bookmarks with tag: " tag)
                    :path-format-fn (partial uri/tag-page params)}
    :search-bookmarks {:page-title (str "Search results for: " search-query)
                       :path-format-fn (partial uri/search params)}))

(defn current-page [page]
  (if (string/blank? page) 1
      (Integer/parseInt page)))

(defn parse-search-terms [search-query]
  (when-not (string/blank? search-query)
    (string/split search-query #" ")))


(defn list-view
  [username
   {:keys [page] :as params}
   {:keys [bookmarks num-pages] :as paginated-bookmarks}
   view-type]
  (let [{:keys [page-title path-format-fn]} (view-type-info view-type params)]
    (layout/user-view page-title
                      username
                      (views/list-all bookmarks
                                      num-pages
                                      page
                                      path-format-fn))))

(defn valid-page-number? [{:keys [page] :as params}]
  (pos? page))

(defn- bookmarks-list
  [view-type {:keys [session] :as request}]
  (let [username (:username session)
        search-terms (parse-search-terms (get-in request [:params :search-query]))
        params (-> (:params request)
                   (update :page current-page)
                   (assoc :search-terms search-terms))]
    (if (valid-page-number? params)
      (let [paginated-bookmarks (fetch-bookmarks username params)]
        (-> (res/response (list-view username params paginated-bookmarks view-type))
            (assoc :headers {"Content-Type" "text/html"})))
      (assoc (res/redirect "/bookmarks")
             :flash {:error-msg "Invalid page number"}))))

(def all-bookmarks-view (partial bookmarks-list :all-bookmarks))
(def tag-bookmarks-view (partial bookmarks-list :tag-bookmarks))
(def search-bookmarks-view (partial bookmarks-list :search-bookmarks))

(defn create-view
  [request]
  (let [username (get-in request [:session :username])
        error-msg (get-in request [:flash :error-msg])]
    (-> (res/response (layout/user-view "Add Bookmark"
                                        username
                                        (views/add-bookmark anti-forgery/*anti-forgery-token*
                                                            "/bookmarks/add"
                                                            {})
                                        :error-msg error-msg))
        (assoc-in [:headers "Content-Type"] "text/html"))))

(defn edit-view
  [{:keys [params] :as request}]
  (let [username (get-in request [:session :username])
        user      (hutils/get-user request)
        bookmark-id (Integer/parseInt (:bookmark-id params))
        bookmark (bm-db/fetch-bookmark bookmark-id (:id user))
        error-msg (if bookmark
                    (get-in request [:flash :error-msg])
                    "The bookmark you're trying to edit does not exist.")]
    (-> (res/response (layout/user-view "Edit Bookmark"
                                        username
                                        (views/add-bookmark anti-forgery/*anti-forgery-token*
                                                            "/bookmarks/edit"
                                                            bookmark)
                                        :error-msg error-msg))
        (assoc-in [:headers "Content-Type"] "text/html"))))
