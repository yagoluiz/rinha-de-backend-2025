(ns rinha-de-backend-2025.workers.processor
  (:require [clojure.core.async :refer [chan go go-loop pipeline-async >! <! put!]]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            [rinha-de-backend-2025.config :as config])
  (:import (java.time Instant)))

(def ^:private job-chan (chan 100))
(def ^:private out-chan (chan 100))

(defn producer [payment]
  (go (>! job-chan payment)))

(defn ^:private send-payment [payment callback]
  (let [processor-url (config/processor-default-url)
        payment-url   (str processor-url "/payments")
        requested-at  (str (Instant/now))
        payload       {:correlationId (:correlationId payment)
                       :amount        (:amount payment)
                       :requestedAt   requested-at}]
    (http/request {:url     payment-url
                   :method  :post
                   :headers {"Content-Type" "application/json"}
                   :body    (json/generate-string payload)}
                  (fn [{:keys [status error body]}]
                    (let [success? (= status 200)
                          result   (if success?
                                     {:success true :response body}
                                     {:success false :reason (or error (str "HTTP " status))})]
                      (callback result))))))

(defn ^:private process-payment [payment _]
  (send-payment payment
                (fn [result]
                  (let [processed-payment (if (:success result)
                                            (-> payment
                                                (assoc :status :success)
                                                (assoc :response (:response result)))
                                            (-> payment
                                                (assoc :status :failed)
                                                (assoc :error (:reason result))))]
                    (println "Processed payment =>" processed-payment)
                    (put! out-chan processed-payment)))))

(defn start-consumers [total-consumers]
  (pipeline-async total-consumers out-chan process-payment job-chan)

  (go-loop []
    (when-let [result (<! out-chan)]
      (println "Process result =>" result)
      (recur))))
