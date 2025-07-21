(ns rinha-de-backend-2025.main
  (:require [rinha-de-backend-2025.server :as server]
            [rinha-de-backend-2025.workers.processor :as processor])
  (:gen-class))

(defn -main [& _]
  (println "Starting Rinha de Backend 2025...")
  (processor/start-consumers! 4)
  (server/start-server!))
