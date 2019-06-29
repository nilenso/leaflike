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

(def bookmark-with-collaborators {:title         "abc"
                                  :url           "http://abc.com"
                                  :tags          ["abc" "random" "test" "music" "something"]
                                  :collaborators ["removetestuser2"]})

(defn user-id [{:keys [email username]}]
  (:id (user-db/get-user-if-exists email username)))

(deftest bookmark-creation-test
  (let [user {:username "creationtest"
              :password "c"
              :email    "creation-test@c.com"}
        user2 {:username "removetestuser2"
               :password "c"
               :email    "remove-test-user-2@c.com"}]
    (user/signup {:params user})
    (user/signup {:params user2})

    (testing "create bookmark success"
      (let [{:keys [status flash]} (bm/create {:params  bookmark-with-collaborators
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (some
              #(= (:title %) (:title bookmark))
              (bm-db/fetch-bookmarks-for-user (user-id user))))

        (is (empty? (:error-msg flash)))))

    (testing "create bookmark with collaborators success"
      (let [{:keys [status flash]} (bm/create {:params  bookmark-with-collaborators
                                               :session {:username (:username user)}})
            bookmark-id (:id (first (bm-db/fetch-bookmarks-for-user (:id user))))]
        (is (= 302 status))
        (let [collaborator-ids (map :user_id (bm-db/get-collaborator-ids bookmark-id))]
          (is (= 1 (count collaborator-ids)))
          (is (= (:username user2) (first (map :username (bm-db/get-collaborators-from-ids collaborator-ids))))))
        (is (empty? (:error-msg flash)))))

    (testing "create bookmark with double slash in url"
      (let [url "https://web.archive.org/web/http://example.com"
            {:keys [status flash]} (bm/create {:params  (assoc bookmark :url url)
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (some
              #(= (:url %) url)
              (bm-db/fetch-bookmarks-for-user (user-id user))))
        (is (empty? (:error-msg flash)))))

    (testing "create bookmark success without tags"
      (let [{:keys [status flash]} (bm/create {:params  (dissoc bookmark :tags)
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (= 4 (count (bm-db/fetch-bookmarks-for-user (user-id user)))))
        (is (empty? (:error-msg flash)))))

    (testing "create bookmark failed"
      (let [{:keys [status flash]} (bm/create {:params  (dissoc bookmark :url)
                                               :session {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid fields: url"))))

    (testing "creation succeeds when tag is a string"
      (let [{:keys [status flash]} (bm/create {:params  (assoc bookmark :tags "boo")
                                               :session {:username (:username user)}})]
        (is (= status 302))
        (is (empty? (:error-msg flash)))))))

(deftest bookmarks-list-test
  (let [user {:username "listtest"
              :password "c"
              :email    "list-test@c.com"}]
    (user/signup {:params user})

    (bm/create {:params  bookmark
                :session {:username (:username user)}})
    (bm/create {:params  (dissoc bookmark :tags)
                :session {:username (:username user)}})
    (testing "list all bookmarks"
      (let [response (bm-db/fetch-bookmarks-for-user (user-id user))]
        (is (= 2 (count response)))))

    (testing "list bookmark by id, wrong input in id"
      (let [{:keys [status flash]} (bm/list-by-creator-id {:route-params {:id "2abc"}
                                                           :session      {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "list bookmark by id"
      (let [bookmark-id (:id (first (bm-db/fetch-bookmarks-for-user (user-id user))))
            response (bm/list-by-creator-id {:route-params {:id (str bookmark-id)}
                                             :session      {:username (:username user)}})]
        (is (= 1 (count response)))))))

(deftest bookmarks-delete-test
  (let [user {:username "deletetest"
              :password "c"
              :email    "delete-test@c.com"}]
    (user/signup {:params user})
    (bm/create {:params  bookmark
                :session {:username (:username user)}})

    (testing "delete bookmark"
      (let [bookmark-id (:id (first (bm-db/fetch-bookmarks-for-user (user-id user))))
            {:keys [status flash]} (bm/delete {:route-params {:id (str bookmark-id)}
                                               :session      {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "delete bookmark failed, invalid input"
      (let [{:keys [status flash]} (bm/delete {:route-params {:id "1jgk"}
                                               :session      {:username (:username user)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "delete bookmark"
      (let [bookmark-id (-> (bm-db/fetch-bookmarks-for-user (user-id user))
                            first
                            :id
                            str)
            {:keys [status flash]} (bm/delete {:params  {:id bookmark-id}
                                               :session {:username (:username user)}})]
        (is (= 302 status))
        (is (empty? (:error-msg flash)))
        (is (= (:success-msg flash) "Successfully deleted bookmark"))))))

(deftest bookmark-collaborator-remove-test
  (let [user {:username "removetestuser1"
              :password "c"
              :email    "remove-test-user-1@c.com"}
        user2 {:username "removetestuser2"
               :password "c"
               :email    "remove-test-user-2@c.com"}]
    (user/signup {:params user})
    (user/signup {:params user2})
    (bm/create {:params  bookmark-with-collaborators
                :session {:username (:username user)}})

    (testing "remove bookmark"
      (let [bookmark-id (:id (first (bm-db/fetch-bookmarks-for-user (user-id user2))))
            {:keys [status flash]} (bm/delete {:route-params {:id (str bookmark-id)}
                                               :session      {:user (:username user2)}})]

        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "remove bookmark failed, invalid input"
      (let [{:keys [status flash]} (bm/delete {:route-params {:id "42-5/7"}
                                               :session      {:username (:username user2)}})]
        (is (= status 302))
        (is (= (:error-msg flash) "Invalid bookmark id"))))

    (testing "remove bookmark"
      (let [bookmark-id (-> (bm-db/fetch-bookmarks-for-user (user-id user2))
                            first
                            :id
                            str)
            {:keys [status flash]} (bm/remove {:params  {:id bookmark-id}
                                               :session {:username (:username user2)}})]

        (is (= 302 status))
        (is (empty? (:error-msg flash)))
        (is (= (:success-msg flash) "Successfully removed bookmark"))))))