(ns rinha-de-backend-2025.db.payments
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [rinha-de-backend-2025.config :as config])
  (:import (java.sql Timestamp)
           (java.time Instant)
           (org.postgresql.util PGobject)))

(defonce ^:private datasource
         (jdbc/get-datasource
          {:dbtype   "postgresql"
           :dbname   (config/postgres-db)
           :host     (config/postgres-host)
           :port     (config/postgres-port)
           :user     (config/postgres-user)
           :password (config/postgres-password)}))

(defn ^:private str->enum [processor]
  (doto (PGobject.)
    (.setType "processor_type")
    (.setValue (name processor))))

(defn ^:private instant->timestamp [instant]
  (-> instant
      Instant/parse
      Timestamp/from))

(defn insert! [{:keys [details]}]
  (->> {:correlation_id (parse-uuid (:correlationId details))
        :amount         (:amount details)
        :processor      (str->enum (:processor details))
        :requested_at   (instant->timestamp (:requestedAt details))}
       (sql/insert! datasource :payments)))

(defn ^:private summary-row->map [{:keys [processor total_requests total_amount]}]
  [(keyword processor)
   {:totalRequests total_requests
    :totalAmount   total_amount}])

(defn find-summary! [from to]
  (let [query          "WITH filtered_payments AS (
                          SELECT processor, amount
                          FROM payments
                          WHERE requested_at BETWEEN ? AND ?
                       )
                       SELECT
                         processor,
                         COUNT(*) AS total_requests,
                         SUM(amount) AS total_amount
                       FROM filtered_payments
                       GROUP BY processor"
        from-ts        (instant->timestamp from)
        to-ts          (instant->timestamp to)
        results        (jdbc/execute! datasource [query from-ts to-ts]
                                      {:builder-fn next.jdbc.result-set/as-unqualified-maps})
        summary        (->> results
                            (map summary-row->map)
                            (into {}))
        default-values {:totalRequests 0 :totalAmount 0}]
    (-> summary
        (update :default #(or % default-values))
        (update :fallback #(or % default-values)))))

(defn purge! []
  (->> ["TRUNCATE TABLE payments RESTART IDENTITY"]
       (jdbc/execute! datasource)))
