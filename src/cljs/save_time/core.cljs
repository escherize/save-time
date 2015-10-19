(ns save-time.core
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

;; -------------------------
;; Views
(defn page []
  [:div [:h2 "Hello!"]])

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
