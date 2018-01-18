(ns leaflike.functional.auth
  (:require [leaflike.user.auth :as user-auth]
            [leaflike.user.db :as user-db]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user.core :as user-core]))

(use-fixtures :once wrap-setup)

(defn make-request
  [input]
  {:params input
   :session nil
   :query-params {:next nil}})

(deftest signup-auth-test
  (testing "signup success"
    (let [response (user-core/signup (make-request {:username "a"
                                                    :password "b"
                                                    :email "a@b.com"}))]
      (is (= (:status response) 302))
      (is (= {:identity :a} @user-auth/user-session)))))

(deftest login-auth-test
  (user-core/signup (make-request {:username "b"
                                   :password "b"
                                   :email "b@b.com"}))

  (testing "login success"
    (let [response (user-core/login (make-request {:username "b"
                                                   :password "b"}))]
      (is (= (:status response) 200))
      (is (= {:identity :b} @user-auth/user-session))))

  (testing "login failure. wrong password"
    (let [response (user-core/login (make-request {:username "b"
                                                   :password "a"}))]
      (is (= (:status response) 401))
      (is (nil? @user-auth/user-session))))

  (testing "login failure. no user"
    (let [response (user-core/login (make-request {:username "c"
                                                   :password "a"}))]
      (is (= (:status response) 401))
      (is (nil? @user-auth/user-session)))))

(deftest logout
   (user-core/signup (make-request {:username "t"
                                    :password "c"
                                    :email "t@c.com"}))

  (testing "logout success"

    (let [response (user-core/logout (make-request {}))]
      (is (= (:status response) 302))
      (is (nil? @user-auth/user-session))))

  (testing "logout failure. user session is already nil"

    (let [response (user-core/logout (make-request {}))]
      (is (= (:status response) 401))
      (is (nil? @user-auth/user-session)))))
