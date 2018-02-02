(ns leaflike.functional.bookmarks
  (:require [leaflike.bookmarks.core :as bm-core]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user.core :as user-core]))

(use-fixtures :once wrap-setup)

(def user {:username "t"
           :password "c"
           :email    "t@c.com"})

(def bookmark {:title "abc"
               :url   "http://abc.com"
               :tags  "abc random test, music . something"})

(defn make-request
  ([params body]
   {:params params
    :body body
    :session nil
    :query-params {:next nil}})
  ([route-params]
   {:params nil
    :body nil
    :session nil
    :query-params {:next nil}
    :route-params route-params}))

(deftest bookmark-tests
   (user-core/signup (make-request user {}))

  (testing "create bookmark success"
    (let [response (bm-core/create (make-request bookmark {}))]
      (is (and (= false (:error response))
               (= '(1) (:result response))))))

    (testing "create bookmark success without tags"
      (let [response (bm-core/create (make-request (dissoc bookmark :tags) {}))]
        (is (and (= false (:error response))
                 (= '(1) (:result response))))))

  (testing "create bookmark failed"
    (let [response (bm-core/create (make-request (dissoc bookmark :url) {}))]
      (is (and (:error response)
               (= "Invalid params" (:result response))))))

  (testing "list all bookmarks"
    (let [response (bm-core/list-all (make-request {} {}))]
      (is (= 2 (count (:result response))))
      ;; todo - array selective equals
      ))

  (testing "list bookmark by id, wrong input in id"
    (let [response (bm-core/list-by-id (make-request {:id "2abc"}))]
      (is (and (:error response)
               (= "Invalid params" (:result response))))))

  (testing "list bookmark by id"
    (let [response (bm-core/list-by-id (make-request {:id "1"}))]
      (is (and (= false (:error response))
               (= 1 (count (:result response)))))))

  (testing "delete bookmark"
    (let [response (bm-core/delete (make-request {:id "1"}))]
      (is (and (= false (:error response))
               (= '(1) (:result response))))))

  (testing "delete bookmark failed, invalid input"
    (let [response (bm-core/delete (make-request {:id "1jgk"}))]
      (is (and (:error response)
               (= "Invalid params" (:result response))))))

  (testing "delete bookmark"
    (let [response (bm-core/delete (make-request {:id "31"}))]
      (is (and (= false (:error response))
               (= '(0) (:result response)))))))
