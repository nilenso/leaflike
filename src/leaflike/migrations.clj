(ns leaflike.migrations
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [leaflike.config :refer [db-spec]]))

(defn migration-config
  [not-test]
  {:datastore  (jdbc/sql-database   (db-spec))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate
  [not-test]
  (try
    (repl/migrate (migration-config not-test))
    (catch Exception ex
      (println ex))))

(defn rollback
  [not-test]
  (try
    (repl/rollback (migration-config not-test))
    (catch Exception ex
      (println ex))))

(defn rollback-all
  [not-test]
  (try
    (let [count-mig (-> (migration-config not-test) :migrations count)]
      (repl/rollback (migration-config not-test) count-mig))
    (catch Exception ex
      (println ex))))
