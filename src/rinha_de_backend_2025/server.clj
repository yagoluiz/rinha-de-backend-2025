(ns rinha-de-backend-2025.server
  (:require [org.httpkit.server :as http-server]
            [rinha-de-backend-2025.http.router :as router]
            [rinha-de-backend-2025.config :as config]))

(defonce ^:private server (atom nil))

(defn start-server! []
  (reset! server (http-server/run-server
                   (router/app-handler)
                   {:port (config/server-port)})))

(defn stop-server! []
  (when @server
    (@server)
    (reset! server nil)))
