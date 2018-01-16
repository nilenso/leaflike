(ns leaflike.core
  (:require [leaflike.server :as server]
            [leaflike.migrations :as migrations]))

(defn setup []
  (migrations/migrate)
  (server/start!))

(defn teardown []
  (migrations/rollback)
  (server/stop!))

(defn -main
  [& args]
  (case (first args)
    "migrate"  (migrations/migrate true)
    "rollback" (migrations/rollback)
    (setup)))
