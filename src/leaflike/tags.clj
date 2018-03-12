(ns leaflike.tags
  (:require [leaflike.tags.db :as tags-db]
            [leaflike.tags.views :as tags-views]
            [leaflike.layout :as layout]
            [leaflike.handler-utils :as hutils]
            [ring.util.response :as res]))

(defn fetch-tags
  [username]
  (let [user (hutils/get-user {:session {:username username}})]
    (tags-db/fetch-tags {:user-id (:id user)})))

(defn all-tags-view
  [username tags]
  (layout/user-view "All tags"
                    username
                    (tags-views/list-all tags)))

(defn tags-list
  [{:keys [session] :as request}]
  (let [username (:username session)
        tags (fetch-tags username)]
    (-> (res/response (all-tags-view username tags))
        (assoc :headers {"Content-Type" "text/html"}))))
