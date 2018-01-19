(ns leaflike.core
  (:require [leaflike.server :as server]
            [leaflike.migrations :as migrations]))

(defn setup []
  (migrations/migrate true)
  (server/start!))

(defn teardown []
  (migrations/rollback true)
  (server/stop!))

(defn -main
  [& args]
  (case (first args)
    "migrate"  (migrations/migrate true)
    "rollback" (migrations/rollback true)
    (setup)))
