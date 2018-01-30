(ns leaflike.functional.auth
  (:require [leaflike.user.auth :as user-auth]
            [leaflike.fixtures :refer [wrap-setup]]
            [clojure.test :refer :all]
            [leaflike.user.core :as user-core]))

(use-fixtures :once wrap-setup)

(def signup user-core/signup)
(def login  user-core/login)
(def logout user-core/logout)

(defn make-request
  [input & {:keys [cookie]}]
  (let [base-request {:params input
                      :session nil
                      :query-params {:next nil}}]
    (if cookie
      (assoc-in base-request [:headers "cookie"] cookie)
      base-request)))

(deftest signup-auth-test
  (testing "signup success"
    (let [response (signup (make-request {:username "a"
                                          :password "b"
                                          :email "a@b.com"}))]
      (is (= (:status response) 302)))))

(deftest login-auth-test
  (let [response (signup (make-request {:username "b"
                                        :password "b"
                                        :email "b@b.com"}))]

    (testing "login success"
      (let [response (login (make-request {:username "b"
                                           :password "b"}))]
        (is (= (:status response) 302))
        (is (= (get-in response [:session :username]) "b"))))

    (testing "login failure. wrong password"
      (let [response (login (make-request {:username "b"
                                           :password "a"}))]
        (is (= (:status response) 401))
        (is (= (get-in response [:session :username]) nil))))

    (testing "login failure. no user"
      (let [response (login (make-request {:username "c"
                                           :password "a"}))]
        (is (= (:status response) 401))
        (is (= (get-in response [:session :username]) nil))))))

(deftest logout-auth-test
  (let [response (signup (make-request {:username "t"
                                        :password "c"
                                        :email "t@c.com"}))
        session  (:session response)]
    (testing "logout success"
      (let [response (logout (-> (make-request {})
                                 (assoc :session session)))]
        (is (= (:status response) 302))))

    (testing "logout failure. user session is already nil"

      (let [response (logout (-> (make-request {})
                                 (assoc :session {:username nil})))]
        (is (= (:status response) 401))))))
