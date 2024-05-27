(ns rember.core
  (:require [ring.adapter.jetty :as jetty]
            [rember.util :as util]
            [rember.auth :as auth]
            [rember.storage :as storage])
  (:gen-class))

(defonce server (atom nil))

(defn not-found [_req]
  {:status 404 :body "not found" :headers {}})

(defn app [req]
  (let [handler (case (:request-method req)
                  :post (case (:uri req)
                          "/register" (util/json-middleware auth/register-handler)
                          "/login" (util/json-middleware auth/login)
                          not-found)
                  :get (if (= (:uri req) "/stored") storage/list-stored not-found)
                  not-found)]
    (handler req)))

(defn run-server []
  (reset! server
          (jetty/run-jetty (fn [req] (app req))
                           {:port 3001
                            :join? false})))

(defn stop-server []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(defn -main
  "Runs rember server"
  []
  (run-server))
