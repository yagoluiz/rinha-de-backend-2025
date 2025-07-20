(ns rinha-de-backend-2025.http.handlers
  (:require [rinha-de-backend-2025.workers.processor :as processor])
  (:import (java.time Instant)))

(defn payment-request! [request]
  (let [payment (:body request)]
    (processor/producer! payment)
    {:status 202}))

(defn payment-summary! [request]
  (let [params (:params request)
        from   (Instant/parse (:from params))
        to     (Instant/parse (:to params))
        _      (println "Payment summary request from" from "to" to)]
    {:status 200}))
