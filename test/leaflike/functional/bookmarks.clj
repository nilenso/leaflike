(ns leaflike.functional.bookmarks
  (:require [leaflike.bookmarks :as bm]
            [leaflike.bookmarks.db :as bm-db]
            [leaflike.user.db :as user-db]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user :as user])
  (:import clojure.lang.ExceptionInfo))

(use-fixtures :once wrap-setup)

(def bookmark {:title "abc"
               :url   "http://abc.com"
               :tags  ["abc" "random" "test" "music" "something"]})

(defn user-id [{:keys [email username]}]
  (:id (user-db/get-user-if-exists email username)))

(deftest bookmark-creation-test
  (let [user {:username "creationtest"
              :password "c"
              :email    "creation-test@c.com"}
        _     (user/signup {:params user})]

    (testing "create bookmark success"
      (let [{:keys [status flash]} (bm/create {:params bookmark
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (some
             #(= (:title %) (:title bookmark))
             (bm-db/fetch-bookmarks-for-user (user-id user))))
        (is (empty? (:error-msg flash)))))

    (testing "create bookmark with double slash in url"
      (let [url "https://web.archive.org/web/http://example.com"
            {:keys [status flash]} (bm/create {:params (assoc bookmark :url url)
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (some
             #(= (:url %) url)
             (bm-db/fetch-bookmarks-for-user (user-id user))))
        (is (empty? (:error-msg flash)))))

    (testing "create bookmark success without tags"
      (let [{:keys [status flash]} (bm/create {:params (dissoc bookmark :tags)
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (= 3 (count (bm-db/fetch-bookmarks-for-user (user-id user)))))
        (is (empty? (:error-msg flash)))))

    (testing "create bookmark failed"
      (let [{:keys [status flash]} (bm/create {:params (dissoc bookmark :url)
                                               :session {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid fields: url"))))

    (testing "creation succeeds when tag is a string"
      (let [{:keys [status flash]} (bm/create {:params (assoc bookmark :tags "boo")
                                               :session {:username (:username user)}})]
        (is (= status 302))
        (is (empty? (:error-msg flash)))))))

(deftest bookmarks-list-test
  (let [user {:username "listtest"
              :password "c"
              :email    "list-test@c.com"}]
    (user/signup {:params user})

    (bm/create {:params bookmark
                :session {:username (:username user)}})
    (bm/create {:params (dissoc bookmark :tags)
                :session {:username (:username user)}})
    (testing "list all bookmarks"
      (let [response (bm-db/fetch-bookmarks-for-user (user-id user))]
        (is (= 2 (count response)))))

    (testing "list bookmark by id, wrong input in id"
      (let [{:keys [status flash]} (bm/list-by-id {:route-params {:id "2abc"}
                                                   :session {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "list bookmark by id"
      (let [bookmark-id (:id (first (bm-db/fetch-bookmarks-for-user (user-id user))))
            response (bm/list-by-id {:route-params {:id (str bookmark-id)}
                                     :session {:username (:username user)}})]
        (is (= 1 (count response)))))))

(deftest bookmarks-delete-test
  (let [user {:username "deletetest"
              :password "c"
              :email    "delete-test@c.com"}]
    (user/signup {:params user})
    (bm/create {:params bookmark
                :session {:username (:username user)}})

    (testing "delete bookmark"
      (let [bookmark-id (:id (first (bm-db/fetch-bookmarks-for-user (user-id user))))
            {:keys [status flash]} (bm/delete {:route-params {:id (str bookmark-id)}
                                               :session {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "delete bookmark failed, invalid input"
      (let [{:keys [status flash]} (bm/delete {:route-params {:id "1jgk"}
                                               :session {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "delete bookmark"
      (let [bookmark-id (-> (bm-db/fetch-bookmarks-for-user (user-id user))
                            first
                            :id
                            str)
            {:keys [status flash]} (bm/delete {:params {:id bookmark-id}
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (empty? (:error-msg flash)))))))

(def all-bookmarks [{:title "t1"
                     :url   "http://abc1.com"
                     :read? false
                     :favorite? false}
                    {:title "t2"
                     :url   "http://abc2.com"
                     :read? true
                     :favorite? false}
                    {:title "t3"
                     :url   "http://abc3.com"
                     :read? false
                     :favorite? true}
                    {:title "t4"
                     :url   "http://abc4.com"
                     :read? true
                     :favorite? true}
                    {:title "t5"
                     :url   "http://abc5.com"
                     :read? false
                     :favorite? false}
                    {:title "t6"
                     :url   "http://abc6.com"
                     :read? false
                     :favorite? false}])

(deftest bookmarks-readfav-list-test
  (let [user {:username "readfavtest"
              :password "readfavtest"
              :email    "readfavtest-test@c.com"}]

    (user/signup {:params user})

    (bm/create {:params  (all-bookmarks 0)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 1)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 2)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 3)
                :session {:username (:username user)}})

    (testing "list all bookmarks"
      (let [response (bm-db/fetch-bookmarks-for-user (user-id user))]
        (is (= 4 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))

    (testing "list all read bookmarks"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :read? true
                                             :limit 10
                                             :offset 0})]
        (is (= 2 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))

    (testing "list all favorite bookmarks"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :favorite? true
                                             :limit 10
                                             :offset 0})]
        (is (= 2 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))

    (testing "list all read & favorite bookmarks"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :read? true
                                             :favorite? true
                                             :limit 10
                                             :offset 0})]
        (is (= 1 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))))

(deftest mark-read-test
  (let [user {:username "markreadtest"
              :password "markreadtest"
              :email    "markreadtest-test@c.com"}]
    (user/signup {:params user})

    (bm/create {:params  (all-bookmarks 0)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 4)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 5)
                :session {:username (:username user)}})

    (testing "list all read bookmarks (before)"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :read? true
                                             :limit 10
                                             :offset 0})]
        (is (= 0 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))

    (testing "Mark read action"
      (let [bm-id (-> (bm-db/fetch-bookmarks-for-user (user-id user)) first :id str)
            {:keys [status flash]} (bm/mark-read {:params {:id bm-id
                                                           :read "true"}
                                                  :session {:username (:username user)}})]
        (is (= 302 status))
        (is (= (:error-msg flash) nil) (str "Invalid bookmark id = " bm-id))))

    (testing "list all read bookmarks (after)"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :read? true
                                             :limit 10
                                             :offset 0})]
        (is (= 1 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))))

(deftest mark-fav-test
  (let [user {:username "markfavtest"
              :password "markfavtest"
              :email    "markfavtest-test@c.com"}]
    (user/signup {:params user})

    (bm/create {:params  (all-bookmarks 0)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 4)
                :session {:username (:username user)}})

    (bm/create {:params  (all-bookmarks 5)
                :session {:username (:username user)}})

    (testing "list all fav bookmarks (before)"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :favorite? true
                                             :limit 10
                                             :offset 0})]
        (is (= 0 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))

    (testing "Mark fav action"
      (let [bm-id (-> (bm-db/fetch-bookmarks-for-user (user-id user)) first :id str)
            {:keys [status flash]} (bm/mark-favorite {:params {:id bm-id
                                                               :favorite "true"}
                                                      :session {:username (:username user)}})]
        (is (= 302 status))
        (is (= (:error-msg flash) nil) (str "Invalid bookmark id = " bm-id))))

    (testing "list all fav bookmarks (after)"
      (let [response (bm-db/fetch-bookmarks {:user-id (user-id user)
                                             :favorite? true
                                             :limit 10
                                             :offset 0})]
        (is (= 1 (count response)) (str "Incorrect returned bookmarks = "
                                        (vec response)))))))