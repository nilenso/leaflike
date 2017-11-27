(ns leaflike.core
  (require [leaflike.server :as server]
           [leaflike.migrations :as migrations]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn setup []
  (migrations/migrate)
  (server/start!))

(defn teardown []
  (migrations/rollback)
  (server/stop!))

(defn -main
  [& args]
  (condp = (first args)
    "migrate"  (migrations/migrate)
    "rollback" (migrations/rollback)
    (setup)))
