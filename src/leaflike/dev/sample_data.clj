(ns leaflike.dev.sample-data
  "Functions to generate and add dummy bookmarks. To be used for testing
  and demoing."
  (:require [leaflike.bookmarks :as bm]
            [faker.generate :as gen]
            [clojure.string :as str]))

(defn- gen-bookmark
  "Generates a bookmark filled with a random title, URL and tags."
  [& {:keys [word-in-title tags num-tags] :or {num-tags 3}}]
  (let [domain (gen/word)
        rand-sentence (gen/sentence)
        title (str word-in-title " " domain " - " rand-sentence)
        tags (or tags
                 (gen/words {:n num-tags}))
        tags-str (str/join "," tags)]
    {:title title
     :url (str "http://" domain ".com")
     :tags tags-str}))

(defn- add-bookmark
  "Adds `bookmark` as `username`."
  [username bookmark]
  (bm/create {:params bookmark
              :session {:username username}}))

(defn- add-n-bookmarks
  "Adds `num-bookmarks` generated bookmarks as `username`."
  [username num-bookmarks & {:keys [tags word-in-title ]}]
  (println (format "Adding %d bookmarks as user %s" num-bookmarks username))
  (dotimes [_ num-bookmarks]
    (add-bookmark username (gen-bookmark :tags tags :word-in-title word-in-title))))
