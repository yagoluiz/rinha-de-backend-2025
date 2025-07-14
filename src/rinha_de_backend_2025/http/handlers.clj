(ns rinha-de-backend-2025.http.handlers)

(defn payment-request [req]
  (let [payment (:body req)]
    {:status 202
     :body   {:payment payment}}))
