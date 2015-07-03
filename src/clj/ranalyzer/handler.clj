(ns ranalyzer.handler
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.util.response :as resp]))

(defroutes app-routes
  (GET "/" [] (-> (resp/resource-response "index.html")
                  (resp/content-type "text/html")))
  (route/not-found "Not Found"))

(def middleware (-> site-defaults
                 (assoc-in [:static :resources] "/")
                 (assoc-in [:security :anti-forgery] false)))

(def app
  (-> app-routes
      (wrap-defaults middleware)))

