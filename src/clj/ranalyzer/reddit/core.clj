(ns ranalyzer.reddit.core
  (:require [com.stuartsierra.component :as component]
            [reloaded.repl :refer [system]]
            [taoensso.timbre :refer [info]]
            [clojure.core.async :as async]
            [chime :refer [chime-ch]]
            [clj-time.core :as ct]
            [clj-time.periodic :as cp]))

(defn get-random-article
  [chan]
  (async/>!! chan {:name "Some article"}))

(defrecord Reddit [chans delay interval]
  component/Lifecycle

  (start [component]
    (info "Reddit poller started")
    (info (str "Delay: " delay " seconds, interval: " interval " seconds"))

    (let [poison-pill-chan (async/chan)
          event-chan (chime-ch (cp/periodic-seq (-> delay ct/secs ct/from-now) (-> interval ct/seconds)))]

      (assoc component
        :event-chan event-chan

        :loop-chan (async/go
                     (loop []
                       (async/alt!
                         ; Exit loop if value was sent on poison-pill-chan
                         poison-pill-chan nil

                         event-chan ([event]
                                      (get-random-article (:posts-chan chans))
                                      (recur))
                         :priority true))
                     (info "Closing loop channel"))

        :poison-pill-chan poison-pill-chan)))

  (stop [component]
    (async/close! (:event-chan component))
    (async/>!! (:poison-pill-chan component) :stop)
    (async/close! (:poison-pill-chan component))
    (assoc component
      :event-chan nil
      :loop-chan nil
      :poison-pill-chan nil)
    (info "Reddit poller stopped")))

(defn new-reddit [& {:keys [delay interval] :or {delay 0 interval 5}}]
  (->Reddit {} delay interval))

(defrecord RedditChannels []
  component/Lifecycle

  (start [component]
    (assoc component :posts-chan (async/chan)))

  (stop [component]
    (async/close! (:posts-chan component))
    (assoc component :posts-chan nil)))

(defn new-reddit-channels []
  (->RedditChannels))