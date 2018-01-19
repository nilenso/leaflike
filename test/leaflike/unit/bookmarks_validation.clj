(ns leaflike.unit.bookmarks-validation
  (:require  [clojure.test :refer :all]
             [leaflike.bookmarks.validator :refer :all]))

(deftest id-test
  (testing "is id in input"
    (let [a {:id "41"}]
      (is (id? a))))

  (testing "input id is invalid"
    (let [a {:id "abc"}]
      (not (id? a))))

  (testing "id is absent in input"
    (let [a {:not_id "78"}]
      (not (id? a)))))

(deftest title-test
  (testing "Is title in input"
    (let [a {:title "something important"}]
      (is (title? a))))

  (testing "is title absent"
    (let [a {:not_title "no title"}]
      (not (title? a)))))

(deftest url-test
  (testing "is url present in input"
    (let [a {:url "http://google.com"}]
      (is (url? a))))

  (testing "is url absent"
    (let [a {:not_url "http://google.com"}]
      (not (url? a))))

  (testing "is url invalid"
    (let [a {:url "dance with me"}]
      (not (url? a)))))

(deftest bookmark-test
  (testing "is bookmark valid"
    (let [a {:id 20 :url "http://google.com" :title "google"}]
      (not (valid-bookmark? a))))

  (testing "is bookmark valid with tags"
    (let [a {:url "http://google.com" :title "google" :tags "information"}]
      (is (valid-bookmark? a))))

  (testing "is bookmark valid with id?"
    (let [a {:id 20 :url "dance 123" :title "google"}]
      (not (valid-bookmark? a))))

  (testing "is bookmark valid without title?"
    (let [a {:id 20 :url "http://google.com"}]
      (not (valid-bookmark? a)))))
