(ns rember.auth
  (:require
   ; web
   [ring.util.response :refer [response]]
   ; database
   [toucan2.core :as t2]
   ; password hashing
   [caesium.crypto.pwhash :as cs]))

(defn create-session [])

(defn login [_req]
  {:status 200 :body "login endpoint" :headers {}})

(defn create-account [username pass]
  (let [further-hash (cs/pwhash-str pass cs/opslimit-interactive cs/memlimit-interactive)]
    (t2/insert! :models/users :username username :pwhash further-hash)))

; https://github.com/ring-clojure/ring/wiki/Sessions
; TODO: password? :P
(defn register-handler [req]
  (let [{:keys [username pass]} (:json req)
        valid-req (not (or (nil? username) (nil? pass)))]
    (cond
      (not valid-req) {:status 400 :body "bad request" :headers {}}
      (t2/exists? :models/users :username username) {:status 400 :body "username taken" :headers {}}
      :else (do
              (create-account username pass)
              (->
               (response "ok")
               (assoc :session {:username username}))))))

; (defn verify-pwhash [hash]
;   (pwhash/pwhash-str-verify))
