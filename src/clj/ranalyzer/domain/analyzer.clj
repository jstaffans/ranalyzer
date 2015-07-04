(ns ranalyzer.domain.analyzer
  (:require [com.stuartsierra.component :as component]
            [reloaded.repl :refer [system]]
            [taoensso.timbre :refer [info]]
            [clojure.core.async :as async]))

(defn start-analyzer-workers
  "Start the analyzer threads. They are automatically stopped when the input channel closes."
  [chan results]
  (dotimes [i 4]
    (async/thread
      (loop []
        (when-let [post (async/<!! chan)]
          (info (str "Analyzing post in thread " i))
          (swap! results conj post)
          (recur))))))

(defrecord Analyzer [chan results]
  component/Lifecycle

  (start [component]
    (info "Analyzer started")
    (start-analyzer-workers chan results)
    (assoc component :analyzer ::started))

  (stop [component]
    (info "Analyzer stopped")
    (assoc component :analyzer nil)))

(defn new-analyzer []
  (map->Analyzer {:results (atom [])}))


(comment
  (require '[reloaded.repl :as r])
  (count @(get-in r/system [:analyzer :results])))