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


(defn create-session [])

(defn login [req]
  {:status 200 :body "login endpoint" :headers {}})

(defn create-account [username]
  (t2/insert! :models/users :username username))

; https://github.com/ring-clojure/ring/wiki/Sessions
; TODO: password? :P
(defn register-handler [req]
  (let [username (get-in req [:json :username])]
    (cond
      (nil? username) {:status 400 :body "no username provided" :headers {}}
      (t2/exists? :models/users :username username) {:status 400 :body "username taken" :headers {}}
      :else (do
              (create-account username)
              (->
               (response "ok")
               (assoc :session {:username username}))))))

; (defn verify-pwhash [hash]
;   (pwhash/pwhash-str-verify))
