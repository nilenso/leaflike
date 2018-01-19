(ns leaflike.fixtures
  (:require  [clojure.test :refer :all]
             [leaflike.migrations :as migrations]))

(defn setup-test
  []
  (migrations/migrate false))

(defn teardown-test
  []
  (migrations/rollback-all false))

(defn wrap-setup
  [fun]
  (setup-test)
  (fun)
  (teardown-test))
