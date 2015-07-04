(ns ranalyzer.system
  (:require
    [plumbing.core :refer [?>]]
    [ranalyzer.handler :refer [app]]
    [ranalyzer.reddit.core :refer [new-reddit]]
    [ranalyzer.domain.analyzer :refer [new-analyzer]]
    [environ.core :refer [env]]
    [system.core :refer [defsystem]]
    (system.components
      [http-kit :refer [new-web-server]]
      [repl-server :refer [new-repl-server]])
    [com.stuartsierra.component :as component]
    [clojure.core.async :as async]))

(defn make-system []
  (-> (component/system-map
        :reddit-chan (async/chan)
        :reddit (component/using
                  (new-reddit :interval 2)
                  {:chan :reddit-chan})
        :analyzer (component/using
                    (new-analyzer)
                    {:chan :reddit-chan})
        :web (new-web-server (Integer. (env :http-port)) app))
    (?> (env :repl-server) (assoc :repl-server (new-repl-server (Integer. (env :repl-port)))))))

