(ns leaflike.validator
  (:import org.apache.commons.validator.UrlValidator))

(defn is-id?
  [body]
  (let [id (:id body)]
    (and (some? id) (integer? id))))

(defn is-title?
  [body]
  (contains? body :title))

(defn is-url?
  [body]
  (let [validator (UrlValidator.) url (:url body)]
    (and (some? url) (.isValid validator url))))

(defn is-valid-bookmark?
  [body]
  (and (is-id? body) (is-title? body) (is-url? body)))
