(ns rinha-de-backend-2025.db.payments
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [rinha-de-backend-2025.config :as config])
  (:import (java.sql Timestamp)
           (java.time Instant)
           (org.postgresql.util PGobject)))

(defonce datasource
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

(defn insert [{:keys [details]}]
  (->> {:correlation_id (parse-uuid (:correlationId details))
        :amount         (:amount details)
        :processor      (str->enum (:processor details))
        :requested_at   (instant->timestamp (:requestedAt details))}
       (sql/insert! datasource :payments)))
