(ns save-time.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reforms.reagent :include-macros true :as f]
            cljsjs.moment)
  (:import goog.History))

(defn simple-view
  "if data is  (r/atom {:a value})
   Then path is a vector like [:a]"
  [data path & [label placeholder]]
  #_(f/with-options {:form {:horizontal true}})
  (f/form
   (f/number (or label "") data path :placeholder placeholder)))

(defn min->year [min] (/ min (* 60 24 365 1.0)))

(defn years->seconds [year] (* year 365 24 60 60))

(defn humanize [n time-unit]
  (cond
    (= time-unit "years") (.humanize (.duration js/moment (years->seconds n) "seconds"))
    :else (.humanize (.duration js/moment n time-unit))))

(defn inspect [form] [:pre (pr-str form)])

(defonce data (r/atom {:age ""
                       :packages-per-year ""}))

(defn scroll-down [pixels]
  (-> (js/$ "html,body")
      (.animate #js {"scrollTop" (str "+=" pixels "px")})))

(defn page []
  (fn []
    (let [age                (-> data deref :age)
          packages-per-year  (-> data deref :packages-per-year)
          life-span          82
          years-left         (max 10 (- life-span age))
          time-per-package   (min->year 60)
          time-save-per-year (* time-per-package packages-per-year)
          time-saved         (* time-save-per-year years-left)
          filled?            (fn [s] (not= "" s))
          forms-filled?      (and (filled? age)
                                  (filled? packages-per-year))]
      [:div {:style {:margin-top "50px"}}
       [:div.container
        [:div.jumbotron
         [:h3 "How long do you spend packing?"]
         [:p "The average australian spends an hour to ship a package."]]
        [:img {:src "http://take.ms/dEtxI" :style {:width "100%" :margin "50px 0 100px 0"}}]
        [:div.row
         [:div.col-md-6.col-xs-12
          (simple-view data [:age]
                       "How old are you?"
                       "It's okay we won't share.")
          (simple-view data [:packages-per-year]
                       "How many packages do you ship per year?"
                       "Like for xmas, birthdays, etc.")]
         [:div.col-md-6.col-xs-12
          (when (filled? age)
            [:div.ct.panel.panel-default {:style {:padding "20px"}}
             [:p "The average Australian lives to be " life-span " years old."]
             [:p "You only have " (if (= 10 years-left) "a few" years-left)
              " left, so don't waste them packaging."]])]]
        (if forms-filled?
          [:div
           [:div.well {:style {:text-align "center"}}
            [:p "Time saved per year: "]
            [:h1 [:strong (humanize time-save-per-year "years")]]]
           [:div.well {:style {:text-align "center"}}
            [:p "Total Time saved: "]
            [:h1 [:strong (humanize time-saved "years")]]]])]])))

;; -------------------------
;; Initialize app
(defn mount-root []
  (r/render [page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
