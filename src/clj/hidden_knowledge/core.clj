(ns hidden-knowledge.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [cheshire.core :as json]))

;; Simple test routes first
(defroutes app-routes
  (GET "/" [] "Hidden Knowledge API is running!")
  (GET "/health" [] (response {:status "ok" :message "Server running"}))
  (POST "/api/test" [data] 
    (response {:received data :status "success"})))

(def app app-routes)

(defn -main [& args]
  (println "Starting Hidden Knowledge server on port 3000...")
  (jetty/run-jetty app {:port 3000 :join? true}))