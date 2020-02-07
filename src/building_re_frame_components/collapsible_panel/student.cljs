(ns building-re-frame-components.collapsible-panel.student
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize
  (fn [_ _]
    {}))

(rf/reg-event-db
 :panel/toggle
 (fn [db [_ id]]
   (update-in db [:panels id] not)))

(rf/reg-sub
 :panel/state
 (fn [db [_ id]]
   (get-in db [:panels id])))

(defn example-component []
  (let [s (reagent/atom 0)]
    (js/setInterval #(swap! s inc) 1000)
    (fn []
      [:div @s])))

(defn panel [id title child]
  (let [s (reagent/atom {:open false})]
    (fn [id title child]
      (let [open? @(rf/subscribe [:panel/state id])
            child-height (:child-height @s)]
        [:div
         [:div
          {:on-click #(rf/dispatch [:panel/toggle id])
           :style {:background-color "#ddd"
                   :padding "0 1em"}}
          [:div
           {:style {:float "right"}}
           (if open?
             "-"
             "+")]
          title]
         [:div {:style {:max-height (if open?
                                      child-height
                                      0)
                        :transition "max-height 0.8s"
                        :overflow "hidden"}}
          [:div
           {:ref #(when %
                    (swap! s assoc :child-height (.-clientHeight %)))
            :style {:background-color "#eee"
                    :padding "0 1 em"}}
           child]]]))))

(defn ui []
  [:div
   [panel :ex-1 "Example Component" [example-component]]])

(when-some [el (js/document.getElementById "collapsible-panel--student")]
  (defonce _init (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))
