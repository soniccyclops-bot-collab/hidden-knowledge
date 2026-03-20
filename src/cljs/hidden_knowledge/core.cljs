(ns hidden-knowledge.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

;; Events
(rf/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db {:research-query ""
         :search-results []
         :analysis-results {}
         :loading? false}}))

(rf/reg-event-fx
 ::research-query-changed
 (fn [{:keys [db]} [_ query]]
   {:db (assoc db :research-query query)}))

(rf/reg-event-fx
 ::submit-research
 (fn [{:keys [db]} _]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :post
                 :uri "/api/research"
                 :params {:question (:research-query db)}
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::research-success]
                 :on-failure [::research-failure]}}))

(rf/reg-event-fx
 ::research-success
 (fn [{:keys [db]} [_ response]]
   {:db (-> db
            (assoc :search-results (:sources response))
            (assoc :keywords (:keywords response))
            (assoc :loading? false))}))

(rf/reg-event-fx
 ::research-failure
 (fn [{:keys [db]} [_ error]]
   {:db (-> db
            (assoc :error (str "Research failed: " error))
            (assoc :loading? false))}))

(rf/reg-event-fx
 ::analyze-document
 (fn [{:keys [db]} [_ identifier]]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :post
                 :uri "/api/analyze"
                 :params {:identifier identifier}
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::analysis-success]
                 :on-failure [::analysis-failure]}}))

(rf/reg-event-fx
 ::analysis-success
 (fn [{:keys [db]} [_ response]]
   {:db (-> db
            (assoc :analysis-results response)
            (assoc :loading? false))}))

;; Subscriptions
(rf/reg-sub
 ::research-query
 (fn [db _]
   (:research-query db)))

(rf/reg-sub
 ::search-results
 (fn [db _]
   (:search-results db)))

(rf/reg-sub
 ::keywords
 (fn [db _]
   (:keywords db)))

(rf/reg-sub
 ::analysis-results
 (fn [db _]
   (:analysis-results db)))

(rf/reg-sub
 ::loading?
 (fn [db _]
   (:loading? db)))

;; Components
(defn research-input []
  [:div.research-input
   [:h2 "Hidden Knowledge Research Tool"]
   [:p "Discover overlooked historical connections and niche knowledge"]
   [:textarea.query-input
    {:placeholder "Ask a research question (e.g., 'How did they magnetize the first magnets?')"
     :value @(rf/subscribe [::research-query])
     :on-change #(rf/dispatch [::research-query-changed (-> % .-target .-value)])
     :rows 4}]
   [:button.research-button
    {:on-click #(rf/dispatch [::submit-research])
     :disabled @(rf/subscribe [::loading?])}
    (if @(rf/subscribe [::loading?]) "Researching..." "Find Knowledge")]])

(defn keyword-display []
  (when-let [keywords @(rf/subscribe [::keywords])]
    [:div.keywords
     [:h3 "Research Keywords"]
     [:div.keyword-list
      (for [keyword keywords]
        ^{:key keyword}
        [:span.keyword keyword])]]))

(defn source-card [source]
  [:div.source-card
   [:h4.source-title (:title source)]
   [:div.source-meta
    [:span.creator (str "Creator: " (:creator source))]
    [:span.date (str "Date: " (:date source))]
    [:span.downloads (str "Downloads: " (:downloads source))]]
   [:p.description (:description source)]
   [:div.source-actions
    [:a.archive-link
     {:href (str "https://archive.org/details/" (:identifier source))
      :target "_blank"}
     "View on Internet Archive"]
    [:button.analyze-button
     {:on-click #(rf/dispatch [::analyze-document (:identifier source)])}
     "Deep Analyze"]]])

(defn search-results []
  (let [results @(rf/subscribe [::search-results])]
    (when (seq results)
      [:div.search-results
       [:h3 (str "Found " (count results) " Historical Sources")]
       [:div.results-grid
        (for [source results]
          ^{:key (:identifier source)}
          [source-card source])]])))

(defn analysis-panel []
  (when-let [analysis @(rf/subscribe [::analysis-results])]
    [:div.analysis-panel
     [:h3 "Document Analysis"]
     [:div.analysis-grid
      [:div.entities
       [:h4 "Extracted Entities"]
       [:div.entity-section
        [:h5 "Names"]
        [:ul (for [name (take 10 (:names (:entities analysis)))]
               ^{:key name} [:li name])]]
       [:div.entity-section
        [:h5 "Places"]
        [:ul (for [place (take 10 (:places (:entities analysis)))]
               ^{:key place} [:li place])]]
       [:div.entity-section
        [:h5 "Dates"]
        [:ul (for [date (take 10 (:dates (:entities analysis)))]
               ^{:key date} [:li date])]]]
      [:div.keywords-analysis
       [:h4 "Key Terms"]
       [:div.keyword-cloud
        (for [keyword (take 20 (:keywords analysis))]
          ^{:key keyword}
          [:span.analysis-keyword keyword])]]]]))

(defn main-app []
  [:div.app
   [research-input]
   [keyword-display]
   [search-results]
   [analysis-panel]])

;; Initialize
(defn init []
  (rf/dispatch-sync [::initialize-db])
  (rdom/render [main-app] (js/document.getElementById "app")))

(init)