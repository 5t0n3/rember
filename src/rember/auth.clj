(ns rember.auth
  (:require
   [clojure.core.match :refer [match]]
   [ring.util.response :refer [response]]
   [toucan2.core :as t2]
   [caesium.crypto.pwhash :as pwhash]))

(defn login [req]
  (match (:json req)
    {:username username :pass passwd}
    (if-let [{pwhash :pwhash} (t2/select-one :models/users :username username)]
      (if (or (nil? pwhash) (not= (pwhash/pwhash-str-verify pwhash passwd) 0))
        {:status 400 :body "login failed"}
        (->
         (response "ok")
         (assoc :session {:username username}))))
    :else {:status 400 :body "login failed"}))

(defn create-account [username pass]
  (let [further-hash (pwhash/pwhash-str pass pwhash/opslimit-interactive pwhash/memlimit-interactive)]
    (t2/insert! :models/users :username username :pwhash further-hash)))

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
