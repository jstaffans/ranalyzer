(def lein-proj 
  (->> 
   "project.clj" 
   slurp 
   read-string 
   (drop 3) 
   (partition 2) 
   (map vec) 
   (into {})))

(set-env!
 :source-paths   (into #{} (:source-paths lein-proj))
 :resource-paths (into #{} (:resource-paths lein-proj)) 
 :dependencies   (into [] (:dependencies lein-proj)))

(require
 '[adzerk.boot-reload    :refer [reload]]
 '[reloaded.repl :refer [init start stop go reset]]
 '[ranalyzer.systems :refer [dev-system prod-system]]
 '[danielsz.boot-environ :refer [environ]]
 '[system.boot :refer [system run]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port 3000})
   (watch :verbose true)
   (system :sys #'dev-system :auto-start false :hot-reload true :files ["handler.clj"])
   (reload)
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:http-port 3000})
   (run :main-namespace "ranalyzer.core" :arguments [#'dev-system])
   (wait)))

(deftask prod-run
  "Run a prod system from the command line"
  []
  (comp
   (environ :env {:http-port 8008
                  :repl-port 8009})
   (run :main-namespace "ranalyzer.core" :arguments [#'prod-system])
   (wait)))
