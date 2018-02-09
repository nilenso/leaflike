(ns leaflike.functional.bookmarks
  (:require [leaflike.bookmarks.core :as bm-core]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user.core :as user-core])
  (:import clojure.lang.ExceptionInfo))

(use-fixtures :once wrap-setup)

(def user {:username "t"
           :password "c"
           :email    "t@c.com"})

(def bookmark {:title "abc"
               :url   "http://abc.com"
               :tags  "abc, random, test, music, something"})

(deftest bookmark-tests
  (user-core/signup {:params user})

  (testing "create bookmark success"
    (let [response (bm-core/create {:params bookmark
                                    :username (:username user)})]
      (is (= '(1) response))))

  (testing "create bookmark success without tags"
    (let [response (bm-core/create {:params (dissoc bookmark :tags)
                                    :username (:username user)})]
      (is (= '(1) response))))

  (testing "create bookmark failed"
    (is (thrown-with-msg? ExceptionInfo #"Invalid params"
                          (bm-core/create {:params (dissoc bookmark :url)
                                           :username (:username user)}))))

  (testing "list all bookmarks"
    (let [response (bm-core/list-all {:username (:username user)})]
      (is (= 2 (count response)))
      ;; todo - array selective equals
      ))

  (testing "list bookmark by id, wrong input in id"
    (is (thrown-with-msg? ExceptionInfo #"Invalid id"
                          (bm-core/list-by-id {:route-params {:id "2abc"}
                                               :username (:username user)}))))

  (testing "list bookmark by id"
    (let [response (bm-core/list-by-id {:route-params {:id "1"}
                                        :username (:username user)})]
      (is (= 1 (count response)))))

  (testing "delete bookmark"
    (let [response (bm-core/delete {:route-params {:id "1"}
                                    :username (:username user)})]
      (is (= '(1) response))))

  (testing "delete bookmark failed, invalid input"
    (is (thrown-with-msg? ExceptionInfo #"Invalid id"
                          (bm-core/delete {:route-params {:id "1jgk"}
                                           :username (:username user)}))))

  (testing "delete bookmark"
    (let [response (bm-core/delete {:route-params {:id "31"}
                                    :username (:username user)})]
      (is (= '(0) response)))))
