(ns leaflike.core
  (require [leaflike.server :refer [start-server stop-server]]
           [leaflike.migrations :refer [migrate-db rollback-db]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn setup []
  (migrate-db)
  (start-server))

(defn teardown []
  (rollback-db)
  (stop-server))

(defn -main [& args]
  (condp = (first args)
    "start" (setup)
    "stop"  (teardown)))
