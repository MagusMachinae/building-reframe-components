(ns building-re-frame-components.inline-editable-field.student
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize
  (fn [db _]
    (assoc db :movies {"tt0095989"
                       {:title "Return of the Killer Tomatoes!"
                        :description "Crazy old Professor Gangreen has developed a way to make tomatoes look human for a second invasion."}})))

(rf/reg-sub
  :movies
  (fn [db _]
    (:movies db)))

(rf/reg-event-db
 :movie/title
 (fn [db [_ id title]]
   (assoc-in db [:movies id :title] title)))

(rf/reg-event-db
 :movie/description
 (fn [db [_ id description]]
   (assoc-in db [:movies id :description] description)))


(defn inline-editor [text on-change]
  (let [s (reagent/atom {})]
    (fn [text on-change]
      [:span
       (if (:editing? @s)
         [:form {:on-submit #(do
                               (.preventDefault %)
                               (swap! s dissoc :editing?)
                               (when on-change
                                 (on-change (:text @s))))}

          [:input {:type :text
                   :value (:text @s)
                   :on-change #(swap! s assoc
                                      :text (-> % .-target .-value))}]
          [:button "Save"]
          [:button {:on-click #(do
                                (.preventDefault %)
                                (swap! s dissoc :editing?))}
           "Cancel"]]
         [:span
          {:on-click #(swap! s assoc
                             :editing? true
                             :text text)}
          text
          [:span {:style {:font-size "125%"}} "✎"]])])))

(defn ui []
  [:div
   (pr-str @(rf/subscribe [:movies]))
   (for [[movie-id movie] @(rf/subscribe [:movies])]
     [:div {:key movie-id}
      [:h3 [inline-editor (:title movie)
            #(rf/dispatch [:movie/title movie-id %])]]
      [:div [inline-editor (:description movie)
             #(rf/dispatch [:movie/description movie-id %])]]])])

(when-some [el (js/document.getElementById "inline-editable-field--student")]
  (defonce _init (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))
