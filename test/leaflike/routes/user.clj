(ns leaflike.routes.user
  (:require [leaflike.fixtures :refer [wrap-setup]]
            [leaflike.server :refer [app]]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as cheshire]))

#_(use-fixtures :each wrap-setup)

(def app-instance (app))

;; parse some json and get keywords back
(defn parse-body
  [body]
  (cheshire/parse-string body true))

(deftest welcome-test
  (testing "welcome route"
    (let [response (app-instance (-> (mock/request :get "/welcome")))
          body     (parse-body   (:body response))]
      (is (= body {:message "Welcome to Leaflike"})))))
