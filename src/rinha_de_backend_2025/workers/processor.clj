(ns rinha-de-backend-2025.workers.processor
  (:require [clojure.core.async :refer [chan go go-loop pipeline-blocking >! <!]]))

(def ^:private job-chan (chan 100))
(def ^:private out-chan (chan 100))

(defn producer [payment]
  (go (>! job-chan payment)))

(defn ^:private process [payment]
  (assoc payment :status :processed))

(defn start-consumers [total-consumers]
  (pipeline-blocking
    total-consumers
    out-chan
    (map process)
    job-chan)
  (go-loop []
    (when-let [result (<! out-chan)]
      (println "Process result =>" result)
      (recur))))
