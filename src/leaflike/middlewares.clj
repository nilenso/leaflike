(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorization]]
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

(defn auth-middleware
  [handler-fn]
  (-> handler-fn
      wrap-authorization))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
