(ns hidden-knowledge.core
  "Core backend for niche historical knowledge extraction"
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [hickory.core :as hickory]
            [hickory.select :as s]
            [clojure.string :as str]
            [java-time :as time]))

;; Internet Archive API client
(defn search-internet-archive
  "Search Internet Archive for historical documents"
  [query & {:keys [collection mediatype year-start year-end limit]
            :or {collection "texts" mediatype "texts" limit 50}}]
  (let [url "https://archive.org/advancedsearch.php"
        params {:q (str query 
                       (when collection (str " AND collection:" collection))
                       (when mediatype (str " AND mediatype:" mediatype))
                       (when year-start (str " AND year:[" year-start " TO " (or year-end "*") "]")))
                :fl "identifier,title,creator,date,description,subject,downloads"
                :sort "downloads desc"
                :rows limit
                :output "json"}]
    (try
      (-> (http/get url {:query-params params :throw-exceptions false})
          :body
          (json/parse-string true)
          :response
          :docs)
      (catch Exception e
        {:error (.getMessage e)}))))

(defn get-document-text
  "Extract text from Internet Archive document"
  [identifier]
  (let [url (str "https://archive.org/stream/" identifier "/" identifier "_djvu.txt")]
    (try
      (-> (http/get url {:throw-exceptions false})
          :body)
      (catch Exception e
        {:error "Could not extract text"}))))

(defn extract-entities
  "Extract key entities from text (names, places, dates)"
  [text]
  (let [date-pattern #"\b\d{4}\b|\b(?:January|February|March|April|May|June|July|August|September|October|November|December)\s+\d{1,2},?\s+\d{4}\b"
        name-pattern #"\b[A-Z][a-z]+\s+[A-Z][a-z]+\b"
        place-pattern #"\b[A-Z][a-z]+(?:\s+[A-Z][a-z]+)*(?:\s+(?:County|City|State|Province|Kingdom|Empire|Republic))\b"]
    {:dates (re-seq date-pattern text)
     :names (take 20 (distinct (re-seq name-pattern text)))
     :places (take 20 (distinct (re-seq place-pattern text)))}))

(defn analyze-document
  "Full analysis of a document for knowledge extraction"
  [identifier]
  (when-let [text (get-document-text identifier)]
    (when-not (:error text)
      {:identifier identifier
       :text-length (count text)
       :entities (extract-entities text)
       :keywords (-> text
                     (str/lower-case)
                     (str/split #"\W+")
                     (->> (filter #(> (count %) 4))
                          (frequencies)
                          (sort-by second >)
                          (take 50)
                          (map first)))})))

;; Research query processing
(defn research-query
  "Process a research question and find relevant sources"
  [question]
  (let [;; Extract key terms from question
        keywords (-> question
                     str/lower-case
                     (str/replace #"[^\w\s]" "")
                     (str/split #"\s+")
                     (->> (filter #(> (count %) 3))
                          (remove #{"what" "where" "when" "why" "how" "they" "were" "did" "was"})))
        
        ;; Search Internet Archive
        search-results (search-internet-archive
                       (str/join " " keywords)
                       :collection "texts"
                       :year-start 1600
                       :year-end 1950
                       :limit 20)]
    
    {:question question
     :keywords keywords
     :sources search-results
     :timestamp (time/instant)}))

;; API Routes
(defroutes app-routes
  (GET "/" [] (slurp "resources/public/index.html"))
  
  (POST "/api/research" request
    (let [body (:body request)
          question (:question body)]
      {:status 200
       :body (research-query question)}))
  
  (POST "/api/analyze" request
    (let [body (:body request)
          identifier (:identifier body)]
      {:status 200
       :body (analyze-document identifier)}))
  
  (GET "/api/search" [q collection year-start year-end]
    {:status 200
     :body (search-internet-archive q
                                   :collection (or collection "texts")
                                   :year-start year-start
                                   :year-end year-end)})
  
  (route/resources "/")
  (route/not-found "Not found"))

(def app
  (-> app-routes
      wrap-json-body
      wrap-json-response
      wrap-reload))

(defn -main [& args]
  (println "Starting Hidden Knowledge server on port 3000...")
  (jetty/run-jetty app {:port 3000 :join? true}))