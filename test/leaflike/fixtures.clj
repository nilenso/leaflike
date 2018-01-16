(ns leaflike.fixtures
  (:require  [clojure.test :refer :all]
             [leaflike.migrations :as migrations]))

(defn create-db
  []
  (migrations/migrate false))

(defn teardown
  []
  (migrations/teardown))

(defn user-test-fixture
  [func]
  (create-db)
  (func)
  (teardown-db))
