(ns leaflike.migrations
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [leaflike.config :refer [db-spec]]))

(defn migration-config
  []
  {:datastore  (jdbc/sql-database (db-spec))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate
  []
  (try
    (repl/migrate (migration-config))
    (catch Exception ex
      (println ex))))

(defn rollback
  []
  (try
    (repl/rollback (migration-config))
    (catch Exception ex
      (println ex))))

(defn rollback-all
  []
  (try
    (let [count-mig (-> (migration-config) :migrations count)]
      (repl/rollback (migration-config) count-mig))
    (catch Exception ex
      (println ex))))
