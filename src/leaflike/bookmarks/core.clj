(ns leaflike.bookmarks.core
  (:require [leaflike.bookmarks.validator :refer [valid-bookmark? valid-params?]]
            [leaflike.bookmarks.db :as bm-db]))

(defn create
  [request]
  (let [body (-> request :body)]
    (if (valid-bookmark? body)
      (bm-db/create-bookmark)
      {:error "Invalid Data"})))

(defn list-bookmark
  [request]
  (let [params (clojure.walk/keywordize-keys (-> request :query-params))]
    (if (empty? params)
      (bm-db/list-all)
      (if (valid-params? params)
        (bm-db/list-by-params params)
        {:error "Invalid Params"}))))

(defn delete
  [request]
  (let [params (-> request :route-params)]
    (if (valid-params? params)
      (bm-db/delete-bookmark params)
      {:error "Invalid params"})))
