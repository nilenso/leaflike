(defproject leaflike "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.3"]
                 [http-kit "2.2.0"]]
  :main ^:skip-aot leaflike.core
  :target-path "target/%s")
