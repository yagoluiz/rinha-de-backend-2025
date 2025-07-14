(ns rinha-de-backend-2025.http.handlers
  (:require [rinha-de-backend-2025.workers.processor :as processor]))

(defn payment-request [req]
  (let [payment (:body req)]
    (processor/producer payment)
    {:status 202
     :body   {:payment payment}}))
