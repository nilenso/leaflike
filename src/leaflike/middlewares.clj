(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorization]]
            [camel-snake-kebab.core :as csk]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :as st])
  (:import clojure.lang.ExceptionInfo))

(defn wrap-exception-handling
  "Catches an exception and returns the appropriate HTTP response."
  [handler-fn]
  (fn [request]
    (try (handler-fn request)
         (catch Exception e
           (log/error "Uncaught exception: " {:request (prn-str request)
                                              :stacktrace (with-out-str
                                                            (st/print-stack-trace e))})
           {:status 500
            :body "Internal Server error"}))))

(defn- map-keys
  "Map a fn `f` to all keys in a map `m`."
  [f m]
  (into {} (map (fn [[k v]] [(f k) v]) (seq m))))

(defn wrap-kebab-case
  "Convert all keys in the :params map to kebab-case."
  [handler-fn]
  (fn [request]
    (handler-fn (update request
                        :params
                        (partial map-keys csk/->kebab-case)))))

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorization))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
