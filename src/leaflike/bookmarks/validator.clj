(ns leaflike.bookmarks.validator
  (:import org.apache.commons.validator.UrlValidator))

(defn id?
  [body]
  (let [id (:id body)]
    (and (some? id)
         (number? (bigdec id)))))

(defn- title?
  [body]
  (contains? body :title))

(defn- url?
  [body]
  (let [validator (UrlValidator.)
        url (:url body)]
    (and (some? url)
         (.isValid validator url))))

(defn valid-bookmark?
  [body]
  (and (nil?   (:id body))
       (title? body)
       (url?   body)))
