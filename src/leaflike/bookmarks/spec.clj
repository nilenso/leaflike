(ns leaflike.bookmarks.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string])
  (:import org.apache.commons.validator.UrlValidator
           java.lang.NumberFormatException))

(defn id?
  [id]
  (and (some? id)
       (string? id)
       (try (Integer/parseInt id)
            (catch NumberFormatException e false))))

(s/def ::id id?)

(defn title?
  [title]
  (not (string/blank? title)))

(s/def ::title title?)

(defn url?
  [url]
  (let [validator (UrlValidator.)]
    (and (some? url)
         (.isValid validator url))))

(s/def ::url url?)

(s/def ::tags string?)

(s/def ::bookmark (s/keys :req-un [::title ::url]
                          :opt-un [::id ::tags]))

(defn valid-bookmark?
  [bookmark]
  (s/valid? ::bookmark bookmark))
