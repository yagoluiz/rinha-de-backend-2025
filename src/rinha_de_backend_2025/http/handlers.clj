(ns rinha-de-backend-2025.http.handlers
  (:require [rinha-de-backend-2025.db.payments :as db]
            [rinha-de-backend-2025.workers.processor :as processor]))

(defn payment-request! [request]
  (let [payment (:body request)]
    (processor/producer! payment)
    {:status 202}))

(defn payment-summary! [request]
  (let [{:keys [from to]} (:params request)
        response (db/find-summary! from to)]
    {:status 200
     :body   response}))

(defn purge-payments! [_]
  (db/purge!)
  {:status 200})
