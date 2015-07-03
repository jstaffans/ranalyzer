(defproject ranalyzer "0.1.0-SNAPSHOT"
  :description "ranalyzer"
  :license {:name "Proprietary"
            :url "http://www.komoot.de"}

  :source-paths #{"src/clj"}
  :resource-paths #{"resources"}
  :dependencies [[adzerk/boot-reload "0.2.6"      :scope "test"]
                 [environ "1.0.0"]
                 [danielsz/boot-environ "0.0.3"]

                 [org.danielsz/system "0.1.8"]
                 [ring/ring-defaults "0.1.5"]
                 [http-kit "2.1.19"]
                 [compojure "1.3.4"]
                 [com.taoensso/timbre "3.3.1"]
                 [jarohen/chime "0.1.6"]
                 [manifold "0.1.0"]
                 [org.clojure/tools.nrepl "0.2.10"]])
  


