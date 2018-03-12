(ns leaflike.bookmarks.views-test
  (:require [leaflike.bookmarks.views :as views]
            [clojure.test :refer :all]))

(deftest pagination-windowing-test
  (testing "less than min window size"
    (is (= (views/truncated-page-list 3 1) [1 2 3])))

  (testing "one window"
    (is (= (views/truncated-page-list 4 1) [1 2 3 4])))

  (testing "two merged windows"
    (is (= (views/truncated-page-list 8 1) (range 1 9))))

  (testing "three merged windows"
    (is (= (views/truncated-page-list 13 7) (range 1 14))))

  (testing "two windows- ellipsis between first and last window"
    (is (= (views/truncated-page-list 10 1)
           [1 2 3 4 :ellipsis 7 8 9 10]))

    (is (= (views/truncated-page-list 10 10)
           [1 2 3 4 :ellipsis 7 8 9 10])))

  (testing "three windows"
    (is (= (views/truncated-page-list 15 8)
           [1 2 3 4 :ellipsis 6 7 8 9 10 :ellipsis 12 13 14 15])))

  (testing "current page is end of first window"
    (is (= (views/truncated-page-list 20 4)
           [1 2 3 4 5 6 :ellipsis 17 18 19 20])))

  (testing "current page is first of last window"
    (is (= (views/truncated-page-list 20 17)
           [1 2 3 4 :ellipsis 15 16 17 18 19 20]))))
