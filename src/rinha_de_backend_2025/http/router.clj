(ns rinha-de-backend-2025.http.router
  (:require [reitit.ring :as ring]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [rinha-de-backend-2025.http.handlers :as handlers]))

(def middlewares
  [wrap-json-body
   wrap-json-response])

(def routes
  [["/payments" {:post handlers/payment-request}]])

(defn app-handler []
  (ring/ring-handler
    (ring/router routes {:data {:middleware middlewares}})
    ring/create-default-handler))
