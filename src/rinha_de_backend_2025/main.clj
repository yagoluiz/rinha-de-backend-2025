(ns rinha-de-backend-2025.main
  (:require [rinha-de-backend-2025.server :as server])
  (:gen-class))

(defn -main [& _]
  (server/start-server))
