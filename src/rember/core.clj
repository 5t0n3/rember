(ns rember.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]
            [clojure.core.match :refer [match]]
            [rember.database :as database]
            [rember.util :as util]
            [rember.auth :as auth]
            [rember.storage :as storage])
  (:gen-class))

(defonce server (atom nil))

; for some reason the default session store doesn't work??
(defonce sessions (atom {}))

(defn not-found [_req]
  {:status 404 :body "not found" :headers {}})

(defn app [req]
  (let [store (memory-store sessions)
        handler (match req
                  {:request-method :post :uri "/register"} (-> auth/register-handler util/wrap-json-request (wrap-session {:store store}))
                  {:request-method :post :uri "/login"} (-> auth/login util/wrap-json-request (wrap-session {:store store}))
                  {:request-method :get :uri "/entries"} (wrap-session storage/list-handler {:store store})
                  {:request-method :post :uri "/entries"} (wrap-session storage/add-handler {:store store})
                  {:request-method :delete :uri "/entries"} (wrap-session storage/delete-handler {:store store})
                  :else not-found)]
    (handler req)))

; repl testing functions
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
  (database/create-tables!)
  (run-server))
