(ns rember.database
  (:require
   [toucan2.core :as t2]
   [methodical.core :as m]))

(def db-spec
  {:dbtype "h2" :dbname "rember.db"})

(m/defmethod t2/do-with-connection :default
  [_connectable f]
  (t2/do-with-connection db-spec f))

(defn create-tables! []
  ; idk if there's a better way to do this :P
  ; also I love how h2 defaults to uppercasing all identifiers, that's super cool
  (t2/query ["CREATE TABLE IF NOT EXISTS \"users\" (\"id\" INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, \"username\" CHARACTER VARYING(50), \"pwhash\" CHARACTER VARYING);"])
  (t2/query ["CREATE TABLE IF NOT EXISTS \"userdata\" (\"id\" INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, \"userid\" INT REFERENCES \"users\"(\"id\"), \"entry\" CHARACTER LARGE OBJECT);"]))
