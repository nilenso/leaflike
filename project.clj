(defproject leaflike "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.3"]
                 [http-kit "2.2.0"]
                 [ragtime "0.7.2"]
                 [org.postgresql/postgresql "42.1.4"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [bidi "2.1.2"]
                 [ring/ring-json "0.4.0"]
                 [commons-validator "1.5.1"]
                 [honeysql "0.9.1"]
                 [buddy "2.0.0"]
                 [org.clojure/algo.generic "0.1.2"]]
  :main ^:skip-aot leaflike.core
  :target-path "target/%s")
