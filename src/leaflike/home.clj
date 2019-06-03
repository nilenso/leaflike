(ns leaflike.home
  (:require [leaflike.layout :as layout]
            [leaflike.home.db :as hm-db]
            [clj-time.core :as time]
            [leaflike.home.views :as home-views]
            [ring.util.response :as res]))

(defn generate-bookmark-stats-db
  [user-id period]
  (case period
    :week (let [end-date (time/now)
                start-date (time/minus end-date (time/weeks 1))]
            (hm-db/count-bookmarks user-id start-date end-date))
    :month (let [end-date (time/now)
                  start-date (time/minus end-date (time/months 1))]
              (hm-db/count-bookmarks user-id start-date end-date))
    (hm-db/count-bookmarks user-id nil nil)))

(defn home-page
  [{:keys [session] :as request}]
  (let [username (:username session)]
    (-> (res/response (layout/user-view (str "Welcome, " username)
                                        username
                                        (home-views/home-view request)))
        (assoc :headers {"Content-Type" "text/html"}))))