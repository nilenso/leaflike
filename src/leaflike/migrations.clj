(ns leaflike.migrations
  (require [ragtime.jdbc :as jdbc]
           [ragtime.repl :as repl]))

(defn db-spec [] {:connection-uri "jdbc:postgresql://localhost:5432/leaflike"})

(defn migration-config []
  {:datastore  (jdbc/sql-database (db-spec))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (try
    (repl/migrate (migration-config))
    (catch Exception ex
      (println ex))))

(defn rollback []
  (try
    (repl/rollback (migration-config))
    (catch Exception ex
      (println ex))))
