(ns rinha-de-backend-2025.server
  (:require [org.httpkit.server :as http-server]
            [rinha-de-backend-2025.http.router :as router]))

(defonce server (atom nil))

(defn start-server []
  (reset! server (http-server/run-server (router/app-handler) {:port 9999})))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)))
