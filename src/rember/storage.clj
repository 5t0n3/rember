(ns rember.storage
  (:require [ring.util.response :refer [response]]
            [toucan2.core :as t2]
            [cheshire.core :as cc]))

(t2/define-named-query ::user-entries
  {:select [:userdata.entry] :from [:userdata] :join [:users [:= :users.id :userdata.userid]]})

(defn list-stored [req]
  (if-let [username (get-in req [:session :username])]
    (let [entries (t2/select :models/userdata :users.username username ::user-entries)]
      (->> entries
        (map :entry)
        cc/generate-string
        response))
    {:status 401 :body "unauthorized" :headers {}}))
