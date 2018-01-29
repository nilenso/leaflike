(ns leaflike.middlewares
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [leaflike.user.auth :refer [wrap-authorized
                                        wrap-unauthorized]]
            [ring.middleware.json :as json]
            [ring.middleware.session :as session]
            [ring.middleware.session.memory :as mem]
            [ring.middleware.params :as params]
            [ring.middleware.anti-forgery :as anti-forgery]
            [leaflike.user.db :as user-db]))

(defonce ^:private all-sessions (atom {}))

(defn maybe-wrap-csrf
  [handler-fn disable-csrf?]
  (if disable-csrf?
    handler-fn
    (anti-forgery/wrap-anti-forgery handler-fn)))

(defn auth-middleware
  [handler-fn & {:keys [disable-csrf?] :or {disable-csrf? false}}]
  (-> handler-fn
      json/wrap-json-response
      (json/wrap-json-params {:keywords? true :bigdecimals? true})
      wrap-authorized
      wrap-unauthorized
      (maybe-wrap-csrf disable-csrf?)
      params/wrap-params
      (session/wrap-session {:store (mem/memory-store all-sessions)})))

(defn home-middleware
  [handler-fn & {:keys [disable-csrf?] :or {disable-csrf? false}}]
  (-> handler-fn
      json/wrap-json-response
      (json/wrap-json-params {:keywords? true :bigdecimals? true})
      (maybe-wrap-csrf disable-csrf?)
      params/wrap-params
      (session/wrap-session {:store (mem/memory-store all-sessions)})))

(defn with-home-middlewares
  [routes-map]
  (fmap home-middleware routes-map))

(defn with-auth-middlewares
  [route-map]
  (fmap auth-middleware route-map))
