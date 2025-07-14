(defproject rinha-de-backend-2025 "0.1.0-SNAPSHOT"
  :description "Rinha de Backend 2025"
  :url "https://github.com/yagoluiz/rinha-de-backend-2025"
  :dependencies [[org.clojure/clojure "1.12.1"]
                 [http-kit "2.8.0"]
                 [ring/ring-core "1.14.2"]
                 [ring/ring-json "0.5.1"]
                 [metosin/reitit "0.9.1"]]
  :source-paths ["src"]
  :main ^:skip-aot rinha-de-backend-2025.main
  :target-path "target/%s"
  :profiles {:dev     {:dependencies [[nrepl "1.3.1"]]}
             :uberjar {:aot :all}})
