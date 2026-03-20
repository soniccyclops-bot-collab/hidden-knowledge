{:name hidden-knowledge
 :version "0.1.0"
 :description "Full-stack Clojure app for niche historical knowledge extraction and pattern recognition"
 :url "https://github.com/soniccyclops-bot-collab/hidden-knowledge"
 :license {:name "MIT"
           :url "https://opensource.org/licenses/MIT"}
 
 :dependencies [[org.clojure/clojure "1.11.1"]
                [org.clojure/clojurescript "1.11.132"]
                
                ;; Backend
                [ring/ring-core "1.9.6"]
                [ring/ring-jetty-adapter "1.9.6"]
                [compojure "1.7.0"]
                [cheshire "5.11.0"]
                [clj-http "3.12.3"]
                [hickory "0.7.1"]
                [nlp "0.3.0"]
                [clojure.java-time "1.2.0"]
                
                ;; Frontend
                [reagent "1.1.1"]
                [re-frame "1.3.0"]
                [day8.re-frame/http-fx "0.2.4"]]
 
 :plugins [[lein-cljsbuild "1.1.8"]
           [lein-ring "0.12.6"]]
 
 :source-paths ["src/clj" "src/cljs"]
 :resource-paths ["resources"]
 
 :ring {:handler hidden-knowledge.core/app
        :port 3000}
 
 :cljsbuild {:builds [{:id "dev"
                       :source-paths ["src/cljs"]
                       :compiler {:output-to "resources/public/js/app.js"
                                  :output-dir "resources/public/js/out"
                                  :optimizations :none
                                  :source-map true}}]}
 
 :profiles {:dev {:dependencies [[ring/ring-mock "0.4.0"]
                                 [org.clojure/test.check "1.1.1"]]}}}