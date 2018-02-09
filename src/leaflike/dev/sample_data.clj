(ns leaflike.dev.sample-data
  "Functions to generate and add dummy bookmarks. To be used for testing
  and demoing."
  (:require [leaflike.bookmarks.core :as bm-core]
            [faker.internet :refer [domain-word]]
            [faker.lorem :refer [sentences]]
            [clojure.string :as str]))

(defn- gen-bookmark
  "Generates a bookmark filled with a random title, URL and tags."
  [& {:keys [num-tags] :or {num-tags 3}}]
  (let [name (domain-word)
        rand-sentence (first (sentences))
        title (str name " - " rand-sentence)
        tags (for [_ (range num-tags)] (domain-word))
        tags-str (str/join "," tags)]
    {:title title
     :url (str "http://" name ".com")
     :tags tags-str}))

(defn- add-bookmark
  "Adds `bookmark` as `username`."
  [username bookmark]
  (bm-core/create {:params bookmark
                   :username username}))

(defn- add-n-bookmarks
  "Adds `num-bookmarks` generated bookmarks as `username`."
  [username num-bookmarks]
  (println (format "Adding %d bookmarks as user %s" num-bookmarks username))
  (dotimes [_ num-bookmarks]
    (add-bookmark username (gen-bookmark))))
