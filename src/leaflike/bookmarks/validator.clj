(ns leaflike.bookmarks.validator
  (:import org.apache.commons.validator.UrlValidator
           java.lang.NumberFormatException))

(defn id?
  [body]
  (try
    (let [id (Integer/parseInt (:id body))]
      (and (some? id)
           (number? id)))
    (catch NumberFormatException e false)))

(defn title?
  [body]
  (contains? body :title))

(defn url?
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
