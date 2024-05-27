(ns rember.storage
  (:require [ring.util.response :refer [response bad-request]]
            [toucan2.core :as t2]
            [cheshire.core :as cc]
            [clojure.java.io :as io]
            [clojure.string :refer [blank?]]))

(t2/define-named-query ::user-entries
  {:select [:userdata.entry] :from [:userdata] :join [:users [:= :users.id :userdata.userid]]})

(defn list-handler [req]
  (if-let [username (get-in req [:session :username])]
    (let [entries (t2/select :models/userdata :users.username username ::user-entries)]
      (->> entries
           (map :entry)
           cc/generate-string
           response))
    {:status 401 :body "unauthorized" :headers {}}))

(defn add-handler [req]
  (if-let [username (get-in req [:session :username])]
    (let [entry (some-> req :body io/reader slurp)]
      (if (blank? entry)
        (bad-request "bad request")
        (let [{userid :id} (t2/select-one [:models/users :id] :username username)]
          (t2/insert! :models/userdata :userid userid :entry entry)
          {:status 201 :body "entry created" :headers {}})))
    {:status 401 :body "unauthorized" :headers {}}))

(defn delete-handler [req]
  (if-let [username (get-in req [:session :username])]
    (let [entry (some-> req :body io/reader slurp)]
      (if (blank? entry)
        (bad-request "bad request")
        (let [{userid :id} (t2/select-one [:models/users :id] :username username)
              num-deleted (t2/delete! :models/userdata :entry entry :userid userid)]
          (if (> num-deleted 0)
            (response "entry deleted")
            (bad-request "no such entry")))))
    {:status 401 :body "unauthorized" :headers {}}))
