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
