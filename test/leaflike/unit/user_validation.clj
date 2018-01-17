(ns leaflike.unit.user-validation
  (:require  [clojure.test :refer :all]
             [leaflike.user.validator :refer :all]))

(deftest email-test
  (testing "test email is valid"
    (is (email? "abc@ac.com")))

  (testing "email is invalid"
    (not (email? "")))

  (testing "email is invalid 2"
    (not (email? "mxng89723bc  jsha"))))

(deftest uername-test
  (testing "test username is valid"
    (is (username? "namc")))

  (testing "username is invalid"
    (not (username? "    namc124    12")))

  (testing "username is invalid 2"
    (not (username? "b@#."))))
