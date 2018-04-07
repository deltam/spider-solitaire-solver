(ns spider-solitaire-solver.solver
  "スパイダーソリティアを解く"
  (:require [spider-solitaire-solver.core :as ss]))

(defn count-movable-card
  "山の移動可能なカードを数える"
  [stk]
  (count
   (filter ss/open-card? stk)))

(defn all-move
  "可能なカード移動のパターンをすべて返す"
  [deck]
  (apply concat
         (for [from (range 10), to (range 10)
               :let [limit (count-movable-card (nth (:stacks deck) from))]
               :when (not= from to)]
           (map #(vector false from to %)
                (filter (fn [n] (ss/movable? deck from to n))
                        (range 1 (inc limit))))
           )))

(defn valid-move
  "可能な手のリストを返す"
  [deck]
  (let [moves (all-move deck)]
    (if (ss/drawable? deck)
      (conj moves [true 0 0 0])
      moves)))

(defn score
  "デッキの評価値を返す"
  [deck]
  1)

(defn best-next-move
  "最適な次の一手を返す"
  [deck]
  [false 0 1 1])
