(ns rinha-de-backend-2025.http.router
  (:require [reitit.ring :as ring]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [rinha-de-backend-2025.http.handlers :as handlers]))

(def ^:private common-middlewares [[wrap-json-response]])

(def ^:private routes
  [["/payments"
    {:post       handlers/payment-request!
     :middleware (conj common-middlewares
                       [wrap-json-body {:keywords? true}])}]
   ["/payments-summary"
    {:get        handlers/payment-summary!
     :middleware (conj common-middlewares
                       [wrap-params]
                       [wrap-keyword-params])}]
   ["/purge-payments"
    {:post       handlers/purge-payments!
     :middleware common-middlewares}]])

(defn app-handler []
  (ring/ring-handler
   (ring/router routes)
   ring/create-default-handler))
