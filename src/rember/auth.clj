(ns rember.auth
  (:require
    ; web
   [ring.util.response :refer [response]]
    ; database
   [toucan2.core :as t2]
   [methodical.core :as m]
    ; password hashing
   [caesium.crypto.pwhash :as pwhash]
   [cheshire.core :as cc]))

(def db-spec
  {:dbtype "h2" :dbname "rember.db"})

(m/defmethod t2/do-with-connection :default
  [_connectable f]
  (t2/do-with-connection db-spec f))

(defn create-session [])

(defn login [req]
  {:status 200 :body "login endpoint" :headers {}})

(defn create-account [username]
  (t2/insert! :models/users :username username))

; https://github.com/ring-clojure/ring/wiki/Sessions
; TODO: password? :P
(defn register-handler [req]
  (println "req json: " (:json req))
  {:status 200 :body "register endpoint" :headers {}})

; (defn register-handler [req]
;   (let [username (get-in req [:json :username])
;         exists (t2/exists? :models/users :username username)]
;     (if (and username (not exists))
;       (do
;         (create-account username)
;         (->
;           (response "ok")
;           (assoc :session {:username username})))
;       {:status 400 :body "username taken" :headers {}})))

; (defn verify-pwhash [hash]
;   (pwhash/pwhash-str-verify))
