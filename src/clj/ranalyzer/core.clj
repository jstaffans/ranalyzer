(ns ranalyzer.core
  (:gen-class)
  (:require [ranalyzer.system :refer [make-system]]
            [reloaded.repl :refer [go]]))

(defn -main
  []
  (reloaded.repl/set-init! #'make-system)
  (go))

