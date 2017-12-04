(ns leaflike.routes
  (:require [bidi.ring :as bidi]
            [leaflike.db :as db]))


(defn welcome
  [_]
  {:message "Welcome to Leaflike"})

(defn ping
  [request]
  {:ping (-> request :route-params :ping)})

(defn create-bookmark
  [request]
  (db/create-bookmark request))

(defn list-bookmark
  [request]
  (db/list-bookmark (clojure.walk/keywordize-keys (-> request :params))))

(def routes  {""                        {:get welcome}
              ["ping/" :ping]           {:get ping}
              "create-bookmark"         {:post create-bookmark}
              "list-bookmark"           {:get list-bookmark}})

(def handler
  (bidi/make-handler ["/" routes]))
