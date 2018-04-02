(ns spider-solitaire-solver.solver
  (:require [spider-solitaire-solver.core :as ss]))

(defn move-list
  "可能な手のリストを返す"
  [deck]
  (cons
   (if (ss/drawable? deck)
     [true 0 0 0])
   [[false 0 1 1]]))

(defn eval-deck
  "デッキの評価値を返す"
  [deck]
  1)

(defn best-next-move
  "最適な次の一手を返す"
  [deck]
  [false 0 1 1])
