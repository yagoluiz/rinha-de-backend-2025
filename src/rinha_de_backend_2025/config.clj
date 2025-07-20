(ns rinha-de-backend-2025.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private config-edn
  (-> (io/resource "config.edn")
      slurp
      edn/read-string))

(defn ^:private env-or-edn [env-key edn-key]
  (or (System/getenv env-key)
      (get config-edn edn-key)))

(defn server-port []
  (-> (env-or-edn "SERVER_PORT" :server-port)
      Integer/parseInt))

(defn processor-default-url []
  (env-or-edn "PROCESSOR_DEFAULT_URL" :processor-default-url))

(defn processor-fallback-url []
  (env-or-edn "PROCESSOR_FALLBACK_URL" :processor-fallback-url))

(defn postgres-db []
  (env-or-edn "POSTGRES_DB" :postgres-db))

(defn postgres-host []
  (env-or-edn "POSTGRES_HOST" :postgres-host))

(defn postgres-port []
  (-> (env-or-edn "POSTGRES_PORT" :postgres-port)
      Integer/parseInt))

(defn postgres-user []
  (env-or-edn "POSTGRES_USER" :postgres-user))

(defn postgres-password []
  (env-or-edn "POSTGRES_PASSWORD" :postgres-password))
