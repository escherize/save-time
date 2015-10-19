(ns save-time.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            cljsjs.moment)
  (:import goog.History))

(defn atom-input [value]
  [:input {:type "number"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn min->year [min]
  (/ min
     (* 60 24 365 1.0)))

(defn years->seconds [year]
  (* year 365 24 60 60))

(defn humanize [n time-unit]
  (cond
    (= time-unit "years") (.humanize (.duration js/moment (years->seconds n) "seconds"))
    :else (.humanize (.duration js/moment n time-unit))))

(defn page []
  (let [age               (r/atom 30)
        packages-per-year (r/atom 5)]
    (fn []
      (let [life-span          90
            years-left         (max 10 (- life-span @age))
            time-per-package   (min->year 30)
            time-save-per-year (* time-per-package @packages-per-year)
            time-saved         (* time-save-per-year years-left)]
        [:div {:style {:margin-top "50px"
                       :text-align "center"}}
         [:div.container
          [:pre (pr-str
                 {:life-span life-span
                  :years-left years-left
                  :time-per-package time-per-package
                  :time-saved time-saved})]
          [:p "What's your age? " [:br]
           [atom-input age]]
          [:p "How many packages do you send per year? "
           [:br]
           [atom-input packages-per-year]]

          [:div
           (humanize time-per-package "years")
           [:h2 "Time saved per year: "]
           [:pre time-save-per-year]
           [:h1 [:strong (humanize time-save-per-year "years")]]]
          [:div
           [:h2 "Total Time saved: "]
           [:h1 [:strong (humanize time-saved "years")]]]]]))))

;; -------------------------
;; Initialize app
(defn mount-root []
  (r/render [page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
