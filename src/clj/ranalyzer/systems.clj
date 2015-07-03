(ns ranalyzer.systems
  (:require
    [ranalyzer.handler :refer [app]]
    [ranalyzer.reddit.core :refer [new-reddit new-reddit-channels]]
    [ranalyzer.domain.analyzer :refer [new-analyzer]]
    [environ.core :refer [env]]
    [system.core :refer [defsystem]]
    (system.components
      [http-kit :refer [new-web-server]]
      [repl-server :refer [new-repl-server]])
    [com.stuartsierra.component :as component]))

(defn dev-system []
  (-> (component/system-map
        :reddit-channels (new-reddit-channels)
        :reddit (new-reddit :interval 2)
        :web (new-web-server (Integer. (env :http-port)) app)
        :analyzer (new-analyzer))
    (component/system-using
      {:web              []
       :reddit           {:chans :reddit-channels}
       :analyzer         {:chans :reddit-channels}})))

(defn prod-system []
  (-> (component/system-map
        :reddit-channels (new-reddit-channels)
        :reddit (new-reddit :interval 2)
        :web (new-web-server (Integer. (env :http-port)) app)
        :analyzer (new-analyzer)
        :repl-server (new-repl-server (Integer. (env :repl-port))))
    (component/system-using
      {:repl-server      []
       :web              []
       :reddit           {:chans :reddit-channels}
       :analyzer         {:chans :reddit-channels}})))


