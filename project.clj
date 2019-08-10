(defproject sasara-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.3.443"]
                 [environ "1.1.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [ring "1.6.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.0"]
                 [cheshire "5.8.0"]
                 [org.clojure/tools.namespace "0.3.1"]
                 [com.google.cloud/google-cloud-pubsub "0.25.0-beta"]]
  :profiles
  {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]
         :source-paths ["src" "dev"]}
   :uberjar {:main sasara-server.system}})
