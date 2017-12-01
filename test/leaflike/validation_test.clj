(ns leaflike.validation-test
  (:require  [clojure.test :refer :all]
             [leaflike.validator :refer :all]))

(deftest is-id-test
  (testing "Is input ID")
  (let [a {:id 81}]
    (= true (is-id? a))))

(deftest is-id-fail-test
  (testing "Input id invalid")
  (let [a {:id "abc"}]
    (= false (is-id? a))))

(deftest is-id-absent-test
  (testing "id is absent")
  (let [a {:not_id 78}]
    (= false (is-id? a))))

(deftest is-title-test
  (testing "Is input title")
  (let [a {:title "something important"}]
    (= true (is-title? a))))

(deftest is-title-absent-test
  (testing "title is absent")
  (let [a {:not_title "no title"}]
    (= false (is-title? a))))

(deftest is-url-test
  (testing "is url present")
  (let [a {:url "http://google.com"}]
    (= true (is-url? a))))

(deftest is-url-absent-test
  (testing "is url absent")
  (let [a {:not_url "http://google.com"}]
    (= false (is-url? a))))

(deftest is-url-invalid-test
  (testing "is url invalid")
  (let [a {:url "dance with me"}]
    (= false (is-url? a))))

(deftest is-valid-bookmark-test
  (testing "is bookmark valid")
  (let [a {:id 20 :url "http://google.com" :title "google"}]
    (= true (is-valid-bookmark? a))))

(deftest is-valid-bookmark1-test
  (testing "is bookmark valid")
  (let [a {:id 20 :url "http://google.com" :title "google" :tags "information"}]
    (= true (is-valid-bookmark? a))))

(deftest is-valid-bookmark-invalid-test
  (testing "is bookmark valid")
  (let [a {:id 20 :url "dance 123" :title "google"}]
    (= false (is-valid-bookmark? a))))

(deftest is-valid-bookmark-missing-url-test
  (testing "is bookmark valid")
  (let [a {:id 20 :url "http://google.com"}]
    (= false (is-valid-bookmark? a))))
