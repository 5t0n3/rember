(ns rember.storage
  (:require [ring.util.response :refer [response]]))

(defn list-stored [req]
  (if-let [username (get-in req [:session :username])]
    (response (format "list stored route w/ username: %s", username))
    {:status 401 :body "unauthorized" :headers {}}))
