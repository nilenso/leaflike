(ns leaflike.functional.home
  (:require [leaflike.home :as home]
            [leaflike.bookmarks :as bm]
            [leaflike.user :as user]
            [leaflike.user.db :as user-db]
            [clj-time.core :as time]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]))

(use-fixtures :once wrap-setup)

(defn user-id [{:keys [email username]}]
  (:id (user-db/get-user-if-exists email username)))

(def all-bookmarks [{:title      "t1"
                     :url        "http://abc1.com"
                     :read?      false
                     :favorite?  false
                     :created-at (time/minus (time/now) (time/days 2))}
                    {:title      "t2"
                     :url        "http://abc2.com"
                     :read?      true
                     :favorite?  false
                     :created-at (time/minus (time/now) (time/days 2))
                     :read-at    (time/minus (time/now) (time/days 2))}
                    {:title       "t3"
                     :url         "http://abc3.com"
                     :read?       false
                     :favorite?   true
                     :created-at  (time/minus (time/now) (time/days 2))
                     :favorite-at (time/minus (time/now) (time/days 2))}
                    {:title       "t4"
                     :url         "http://abc4.com"
                     :read?       true
                     :favorite?   true
                     :created-at  (time/minus (time/now) (time/days 2))
                     :read-at     (time/minus (time/now) (time/days 2))
                     :favorite-at (time/minus (time/now) (time/days 2))}
                    {:title      "t5"
                     :url        "http://abc5.com"
                     :read?      false
                     :favorite?  false
                     :created-at (time/minus (time/now) (time/days 25))}
                    {:title      "t6"
                     :url        "http://abc6.com"
                     :read?      false
                     :favorite?  false
                     :created-at (time/minus (time/now) (time/days 100))}
                    {:title      "t7"
                     :url        "http://abc7.com"
                     :read?      true
                     :favorite?  false
                     :created-at (time/minus (time/now) (time/days 25))
                     :read-at    (time/minus (time/now) (time/days 24))}
                    {:title       "t8"
                     :url         "http://abc8.com"
                     :read?       false
                     :favorite?   true
                     :created-at  (time/minus (time/now) (time/days 25))
                     :favorite-at (time/minus (time/now) (time/days 24))}
                    {:title      "t9"
                     :url        "http://abc9.com"
                     :read?      true
                     :favorite?  false
                     :created-at (time/minus (time/now) (time/days 100))
                     :read-at    (time/minus (time/now) (time/days 100))}
                    {:title       "t10"
                     :url         "http://abc10.com"
                     :read?       false
                     :favorite?   true
                     :created-at  (time/minus (time/now) (time/days 100))
                     :favorite-at (time/minus (time/now) (time/days 100))}])


(deftest bookmarks-stats-test
  (let [user {:username "statstest"
              :password "statstest"
              :email    "statstest-test@c.com"}]

    (user/signup {:params user})

    ;(bm/create {:params  (all-bookmarks 0)
    ;            :session {:username (:username user)}})
    ;(for [b all-bookmarks]
    ;  (bm/create {:params  b
    ;              :session {:username (:username user)}}))
    (testing "Create all bookmarks"
      (is (every? true?
                  (for [b all-bookmarks]
                    (let [{:keys [status flash]} (bm/create {:params  b
                                                             :session {:username (:username user)}})]
                      (and (= 302 status)
                           (empty? (:error-msg flash))))))
          "Bookmark creation failed"))

    (testing "Check bookmark stats for week"
      (let [stats (home/generate-bookmark-stats-db (user-id user) :week)]
        (is (and (= 4 (:total stats))
                 (= 2 (:read stats))
                 (= 2 (:favorite stats)))
            (str "Incorrect stats = "
                 (vec stats)))))
    (testing "Check bookmark stats for month"
      (let [stats (home/generate-bookmark-stats-db (user-id user) :month)]
        (is (and (= 7 (:total stats))
                 (= 3 (:read stats))
                 (= 3 (:favorite stats)))
            (str "Incorrect stats = "
                 (vec stats)))))
    (testing "Check bookmark stats for all"
      (let [stats (home/generate-bookmark-stats-db (user-id user) :all)]
        (is (and (= 10 (:total stats))
                 (= 4 (:read stats))
                 (= 4 (:favorite stats)))
            (str "Incorrect stats = "
                 (vec stats)))))))

