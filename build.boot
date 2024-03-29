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
 '[adzerk.boot-reload :refer [reload]]
 '[reloaded.repl :refer [init start stop go reset]]
 '[ranalyzer.system :refer [make-system]]
 '[danielsz.boot-environ :refer [environ]]
 '[system.boot :refer [system run]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port 3000})
   (watch :verbose true)
   (system :sys #'make-system :auto-start false :hot-reload true :files ["handler.clj"])
   (reload)
   (repl :server true)))

(deftask prod-run
  "Run a prod system from the command line"
  []
  (comp
   (environ :env {:http-port 8008
                  :repl-server true
                  :repl-port 8009})
   (run :main-namespace "ranalyzer.core")
   (wait)))
