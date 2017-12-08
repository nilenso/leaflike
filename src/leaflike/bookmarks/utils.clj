(ns leaflike.bookmarks.utils
  (:import [java.util Date TimeZone]
           [java.text SimpleDateFormat]
           [java.sql Timestamp]))

(defn get-timestamp
  []
  (let [date (Date.)]
    (TimeZone/setDefault (TimeZone/getTimeZone "UTC"))
    (.format (SimpleDateFormat. "yyyy-mm-dd hh:mm:ss") date)
    (Timestamp. (.getTime date))))

(defn add-created-at
  [body]
  (assoc body :created_at (get-timestamp)))
