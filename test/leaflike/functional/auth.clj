(ns leaflike.functional.auth
  (:require [leaflike.user.auth :as user-auth]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user.core :as user-core]
            [leaflike.middlewares :as middlewares]))

(use-fixtures :once wrap-setup)

(def signup (middlewares/home-middleware user-core/signup))
(def login (middlewares/home-middleware user-core/login))
(def logout (middlewares/auth-middleware user-core/logout))

(defn make-request
  [input & {:keys [cookie]}]
  (let [base-request {:params input
                      :session nil
                      :query-params {:next nil}}]
    (if cookie
      (assoc base-request
             :headers {"cookie" cookie})
      base-request)))

(deftest signup-auth-test
  (testing "signup success"
    (let [response (signup (make-request {:username "a"
                                          :password "b"
                                          :email "a@b.com"}))
          cookie (first (get-in response [:headers "Set-Cookie"]))]
      (is (= (:status response) 302)))))

(deftest login-auth-test
  (let [response (signup (make-request {:username "b"
                                        :password "b"
                                        :email "b@b.com"}))
        cookie (first (get-in response [:headers "Set-Cookie"]))]

    (testing "login success"
      (let [response (login (make-request {:username "b"
                                           :password "b"}))]
        (is (= (:status response) 200))))

    (testing "login failure. wrong password"
      (let [response (login (make-request {:username "b"
                                           :password "a"}))]
        (is (= (:status response) 401))))

    (testing "login failure. no user"
      (let [response (login (make-request {:username "c"
                                           :password "a"}))]
        (is (= (:status response) 401))))))

(deftest logout-auth-test
  (let [response (signup (make-request {:username "t"
                                        :password "c"
                                        :email "t@c.com"}))
        cookie (first (get-in response [:headers "Set-Cookie"]))]
    (testing "logout success"
      (let [response (logout (make-request {}
                                           :cookie cookie))]
        (is (= (:status response) 302))))

    (testing "logout failure. user session is already nil"

      (let [response (logout (make-request {}))]
        (is (= (:status response) 401))))))
