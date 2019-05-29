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
  (let [validator (UrlValidator. UrlValidator/ALLOW_2_SLASHES)]
    (and (some? url)
         (.isValid validator url))))

(s/def ::url url?)

(s/def ::string string?)

(s/def ::tags (s/or
               :nil nil?
               :string string?
               :coll-of-strings (s/coll-of string?)))

(s/def ::read boolean?)

(s/def ::favourite boolean?)

(s/def ::bookmark (s/keys :req-un [::title ::url]
                          :opt-un [::id ::tags ::read ::favorite]))

(defn valid-bookmark?
  [bookmark]
  (s/valid? ::bookmark bookmark))

(defn collect-validation-error-paths
  [bookmark]
  (let [required-keys [:title :url]
        bookmark (reduce (fn [final-bookmark k]
                           (if (get bookmark k)
                             final-bookmark
                             (assoc final-bookmark k nil)))
                         bookmark
                         required-keys)
        problems (:clojure.spec.alpha/problems (s/explain-data ::bookmark bookmark))]
    (reduce (fn [problem-keys problem]
              (concat problem-keys (:path problem)))
            []
            problems)))

(defn describe-errors
  [bookmark]
  (let [error-keys (collect-validation-error-paths bookmark)]
    (str "Invalid fields: " (string/join ", " (map name error-keys)))))
