(ns rember.util
  (:require [cheshire.core :as cc]
            [clojure.java.io :as io]))

(defn try-parse-json [stream]
  (try
    (cc/parse-stream (io/reader stream) true)
    ; TODO: figure out how to catch specifically JsonParseException
    (catch Exception _e
      nil)))

(defn wrap-json-request [handler]
  (fn [req]
    (let [json-body (try-parse-json (:body req))
          updated-req (assoc req :json json-body)]
      (handler updated-req))))
