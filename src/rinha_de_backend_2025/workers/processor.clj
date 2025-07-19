(ns rinha-de-backend-2025.workers.processor
  (:require [clojure.core.async :refer [<! >! chan go go-loop pipeline-async put!]]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            [rinha-de-backend-2025.config :as config])
  (:import (java.time Instant)))

(def ^:private job-chan (chan 100))
(def ^:private out-chan (chan 100))

(defn producer [payment]
  (go (>! job-chan payment)))

(defn ^:private attempt-payment [payment processor callback]
  (let [processor-url (if (= processor :processor-default)
                        (config/processor-default-url)
                        (config/processor-fallback-url))
        payment-url   (str processor-url "/payments")
        requested-at  (str (Instant/now))
        payload       {:correlationId (:correlationId payment)
                       :amount        (:amount payment)
                       :requestedAt   requested-at}]
    (http/request {:url     payment-url
                   :method  :post
                   :headers {"Content-Type" "application/json"}
                   :body    (json/generate-string payload)}
                  (fn [{:keys [status]}]
                    (let [success? (= status 200)
                          details  (-> payload
                                       (assoc :processor processor))
                          result   {:success success? :details details}]
                      (callback result))))))

(defn ^:private process-payment [payment _]
  (let [primary-processor  :processor-default
        fallback-processor :processor-fallback]
    (attempt-payment payment
                     primary-processor
                     (fn [default-result]
                       (if (:success default-result)
                         (put! out-chan default-result)
                         (attempt-payment payment
                                          fallback-processor
                                          (fn [fallback-result]
                                            (when (:success fallback-result)
                                              (put! out-chan fallback-result)))))))))

(defn start-consumers [total-consumers]
  (pipeline-async total-consumers out-chan process-payment job-chan)

  (go-loop []
    (when-let [result (<! out-chan)]
      (println "Process result =>" result)
      (recur))))
