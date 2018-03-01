(ns leaflike.functional.bookmarks
  (:require [leaflike.bookmarks :as bm]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user :as user])
  (:import clojure.lang.ExceptionInfo))

(use-fixtures :once wrap-setup)

(def user {:username "t"
           :password "c"
           :email    "t@c.com"})

(def bookmark {:title "abc"
               :url   "http://abc.com"
               :tags  "abc, random, test, music, something"})

(defn- fetch-all-bookmarks
  [username]
  (loop [all []
         page-num 1]
    (let [{:keys [bookmarks num-pages]} (bm/fetch-bookmarks username {:page page-num})]
      (if (> page-num num-pages)
        all
        (recur (concat all bookmarks)
               (inc page-num))))))

(deftest bookmark-tests
  (user/signup {:params user})

  (testing "create bookmark success"
    (let [{:keys [status flash]} (bm/create {:params bookmark
                                             :session {:username (:username user)}})]
      (is (= 302 status))
      (is (empty? (:error-msg flash)))))

  (testing "create bookmark success without tags"
    (let [{:keys [status flash]} (bm/create {:params (dissoc bookmark :tags)
                                             :session {:username (:username user)}})]
      (is (= 302 status))
      (is (empty? (:error-msg flash)))))

  (testing "create bookmark failed"
    (let [{:keys [status flash]} (bm/create {:params (dissoc bookmark :url)
                                             :session {:username (:username user)}})]
      (is (= status 302))
      (is (= (:error-msg flash) "Invalid bookmark"))))

  (testing "list all bookmarks"
    (let [response (fetch-all-bookmarks (:username user))]
      (is (= 2 (count response)))))

  (testing "list bookmark by id, wrong input in id"
    (let [{:keys [status flash]} (bm/list-by-id {:route-params {:id "2abc"}
                                                 :session {:username (:username user)}})]
      (is (= status 302))
      (is (= (:error-msg flash) "Invalid bookmark id"))))

  (testing "list bookmark by id"
    (let [response (bm/list-by-id {:route-params {:id "1"}
                                   :session {:username (:username user)}})]
      (is (= 1 (count response)))))

  (testing "delete bookmark"
    (let [response (bm/delete {:route-params {:id "1"}
                               :session {:username (:username user)}})]
      (is (= '(1) response))))

  (testing "delete bookmark failed, invalid input"
    (let [{:keys [status flash]} (bm/delete {:route-params {:id "1jgk"}
                                             :session {:username (:username user)}})]
      (is (= status 302))
      (is (= (:error-msg flash) "Invalid bookmark id"))))

  (testing "delete bookmark"
    (let [response (bm/delete {:route-params {:id "31"}
                               :session {:username (:username user)}})]
      (is (= '(0) response)))))
