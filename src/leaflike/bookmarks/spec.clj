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

(s/def ::string string?)

(s/def ::tags (s/or
               :nil nil?
               :string string?
               :coll-of-strings (s/coll-of string?)))

(s/def ::bookmark (s/keys :req-un [::title ::url]
                          :opt-un [::id ::tags]))
