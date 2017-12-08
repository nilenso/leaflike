(ns leaflike.bookmarks.validator
  (:import org.apache.commons.validator.UrlValidator))

(defn is-id?
  [body]
  (let [id (:id body)]
    (and (some? id)
         (number? (bigdec id)))))

(defn is-title?
  [body]
  (contains? body :title))

(defn is-url?
  [body]
  (let [validator (UrlValidator.)
        url (:url body)]
    (and (some? url)
         (.isValid validator url))))

(defn is-valid-bookmark?
  [body]
  (and ((complement is-id?) body)
       (is-title? body)
       (is-url? body)))

(defn is-valid-params?
  [params]
  (or
   (is-id? params)
   (is-title? params)))
