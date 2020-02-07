(ns building-re-frame-components.sortable-table.student
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(def data
  [["Name" "Weapon" "Side" "Height (m)"]
   ["Luke Skywalker" "Blaster" "Good" 1.72]
   ["Leia Organa" "Blaster" "Good" 1.5]
   ["Han Solo" "Blaster" "Good" 1.8]
   ["Obi-Wan Kenobi" "Light Saber" "Good" 1.82]
   ["Chewbacca" "Bowcaster" "Good" 2.28]
   ["Darth Vader" "Light Saber" "Bad" 2.03]])


(rf/reg-event-db
 :initialize
 (fn [db _]
   {:tables {:new-hope {:header (first data)
                        :rows (vec (rest data))}}}))

(rf/reg-sub
 :table
 (fn [db [_ key]]
   (get-in db [:tables key])))

(defn sortable-table [table-key]
  (let [s (reagent/atom {})]
    (fn [table-key]
      (let [table @(rf/subscribe [:table table-key])
            key (:sort-key @s)
            dir (:sort-direction @s)
            rows (cond->>
                  (:rows table)
                  key (sort-by #(nth % key))
                  (= :ascending dir) reverse)
            sorts [key dir]]
        [:div
         [:table
          {:style {:font-size "130%"}}
          [:tr
           (for [[i h] (map vector (range) (:header table))]
             [:th {:style {:line-height ".75em"}
                   :on-click #(cond
                                (= [i :descending] sorts)
                                (swap! s assoc
                                       :sort-direction :ascending)
                                (= [i :ascending] sorts)
                                (swap! s dissoc
                                       :sort-key :sort-direction)
                                :else
                                (swap! s assoc
                                       :sort-key i
                                       :sort-direction :descending))}

              [:div {:style {:display :inline-block}}
               h]
              [:div {:style {:display :inline-block
                             :vertical-align :middle
                             :font-size "90%"}}
               [:div {:style {:color (if (= [i :descending] sorts)
                                       :black
                                       "#aaa")}}
                "▲"]
               [:div {:style {:color (if (= [i :ascending] sorts)
                                       :black
                                       "#aaa")}}
                "▼"]]])]
          (for [row rows]
            [:tr
             (for [v row]
               [:td v])])]]))))

(defn ui []
  [:div
   [sortable-table :new-hope]])

(when-some [el (js/document.getElementById "sortable-table--student")]
  (defonce _init2 (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))
