(ns spider-solitaire-solver.core-test
  (:require [clojure.test :refer :all]
            [spider-solitaire-solver.core :refer :all]))

(deftest gen-deck-test
  (let [deck (gen-deck)
        stacks (:stacks deck)]
    (testing "デッキのカード枚数"
      (is (= 6 (count (nth stacks 0))))
      (is (= 6 (count (nth stacks 1))))
      (is (= 6 (count (nth stacks 2))))
      (is (= 6 (count (nth stacks 3))))
      (is (= 5 (count (nth stacks 4))))
      (is (= 5 (count (nth stacks 5))))
      (is (= 5 (count (nth stacks 6))))
      (is (= 5 (count (nth stacks 7))))
      (is (= 5 (count (nth stacks 8))))
      (is (= 5 (count (nth stacks 9))))
      (is (= 50 (count (:draw deck)))))))

(deftest move-card-test
  (let [deck {:stacks [[(gen-card 1 true)]
                       [(gen-card 2 true)]
                       [(gen-card 4 false) (gen-card 3 true)]
                       [(gen-card 2 true) (gen-card 1 true)]
                       []
                       []
                       []
                       []
                       []
                       []]
              :draw []}]
    (testing "空の山へ移動できる"
      (is (= (move-card deck 0 9 1)
             {:stacks [[]
                       [(gen-card 2 true)]
                       [(gen-card 4 false) (gen-card 3 true)]
                       [(gen-card 2 true) (gen-card 1 true)]
                       []
                       []
                       []
                       []
                       []
                       [(gen-card 1 true)]]
              :draw []})))
    (testing "カードの上に移動できる"
      (is (= (move-card deck 0 1 1)
             {:stacks [[]
                       [(gen-card 2 true) (gen-card 1 true)]
                       [(gen-card 4 false) (gen-card 3 true)]
                       [(gen-card 2 true) (gen-card 1 true)]
                       []
                       []
                       []
                       []
                       []
                       []]
              :draw []})))))

(deftest movable?-test
  (let [deck {:stacks [[(gen-card 1 true)]
                       [(gen-card 2 true)]
                       [(gen-card 4 false) (gen-card 3 true)]
                       [(gen-card 2 true) (gen-card 1 true)]
                       []
                       []
                       []
                       []
                       []
                       []
                       []]
              :draw []}]
    (testing "空の山からはカードは移動できない"
      (is (not (movable? deck 8 9 1))))
    (testing "開いているカードなら空の山へ移動できる"
      (is (movable? deck 0 9 1))
      (is (movable? deck 3 9 2)))
    (testing "カードの上に移動するには降順で連続してないといけない"
      (is (movable? deck 0 1 1))
      (is (movable? deck 3 2 2))
      (is (not (movable? deck 0 2 1))))
    (testing "伏せているカードは一緒に移動できない"
      (is (not (movable? deck 2 9 2)))
      (is (movable? deck 3 9 2)))
    (testing "存在しない枚数のカードは移動できない"
      (is (not (movable? deck 3 9 3))))))

(deftest sorted-card?-test
  (testing "空のカードは揃っていないと判定する"
    (is (not (sorted-card? []))))
  (testing "カードの数字が降順に揃っている"
    (is (sorted-card? [(gen-card 3 false)
                       (gen-card 2 false)
                       (gen-card 1 false)]))
    (is (not (sorted-card? [(gen-card 1 false)
                            (gen-card 2 false)
                            (gen-card 3 false)]))))
  (testing "カードの前後の差は1である"
    (is (sorted-card? [(gen-card 10 false)
                       (gen-card 9 false)
                       (gen-card 8 false)])))
  (is (not (sorted-card? [(gen-card 10 false)
                          (gen-card 8 false)
                          (gen-card 7 false)]))))

(deftest full-card?-test
  (testing
   (is (full-card? [(gen-card 13 true)
                    (gen-card 12 true)
                    (gen-card 11 true)
                    (gen-card 10 true)
                    (gen-card 9 true)
                    (gen-card 8 true)
                    (gen-card 7 true)
                    (gen-card 6 true)
                    (gen-card 5 true)
                    (gen-card 4 true)
                    (gen-card 3 true)
                    (gen-card 2 true)
                    (gen-card 1 true)])
       "1-13までのカードが降順で揃っているか")
    (is  (not (full-card? []))
         "空のカード列はfalseとする")
    (is (not (full-card? [(gen-card 13 true)
                          (gen-card 12 true)
                          (gen-card 11 true)
                          (gen-card 10 true)
                          (gen-card 9 true)
                          (gen-card 8 true)
                          (gen-card 7 true)
                          (gen-card 6 true)
                          (gen-card 5 true)
                          (gen-card 4 true)
                          (gen-card 3 true)
                          (gen-card 2 true)
                          (gen-card 1 true)
                          (gen-card 0 true)]))
        "最小が1より小さいカード列はfalseとする")
    (is (not (full-card? [(gen-card 1 true)
                          (gen-card 2 true)
                          (gen-card 3 true)
                          (gen-card 4 true)
                          (gen-card 5 true)
                          (gen-card 6 true)
                          (gen-card 7 true)
                          (gen-card 8 true)
                          (gen-card 9 true)
                          (gen-card 10 true)
                          (gen-card 11 true)
                          (gen-card 12 true)
                          (gen-card 13 true)]))
        "昇順はfalseとする")))

(deftest open-card?-test
  (testing
   (is (open-card? (gen-card 1 true)))))
