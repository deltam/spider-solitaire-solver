(ns spider-solitaire-solver.core
  "スパイダーソリティアのゲーム操作")

;; トランプ二組
(def cards
  (for [r (flatten (repeat 8 (range 1 14))), s [:c]]
    r))

;; 初期配置の山のカード数
(def stack-count [6 6 6 6 5 5 5 5 5 5])

;; ゲーム履歴
(def deck-history (ref []))

(defn current-deck
  "最新のゲームの状態"
  []
  (peek @deck-history))

(defn init-history
  "ゲーム履歴を初期化する"
  []
  (dosync (ref-set deck-history [])))

(defn push-history
  "ゲーム履歴を追加"
  [d]
  (dosync
   (ref-set deck-history (conj @deck-history d))))

(defn pop-history
  "ゲーム履歴をひとつ戻す"
  []
  (dosync
   (ref-set deck-history (pop @deck-history))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn partition-by-counts
  "指定された個数ごとにアイテムを切り分ける"
  [s counts]
  (if (empty? counts)
    nil
    (let [c (first counts)]
      (cons (take c s)
            (partition-by-counts (drop c s) (rest counts))))))

(defn gen-card
  "カードを生成する"
  [rank  open?]
  {:rank rank
   :open? open?})

(defn open-card?
  "カードは開いているか？"
  [card]
  (:open? card))

(defn close-card?
  "カードは伏せているか？"
  [card]
  (not (open-card? card)))

(defn open-card
  "カードを開く"
  [card]
  (assoc card :open? true))

(defn open-stack-top
  "一番上のカードを開く"
  [stk]
  (if (not-empty stk)
    (let [last-idx (dec (count stk))]
      (update stk last-idx open-card))
    stk))

(defn gen-stack
  "カードを一つの山に積む"
  [cs]
  (open-stack-top (vec cs)))

(defn gen-deck
  "カードの初期配置を作る"
  []
  (let [cs (mapv #(gen-card % false) (shuffle cards))
        stacks (partition-by-counts cs stack-count)
        draw (drop 54 cs)]
    {:stacks (mapv #(gen-stack %) stacks)
     :draw draw}))

(defn init
  "ゲームの初期状態"
  []
  (init-history)
  (push-history (gen-deck)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; move

(defn pop-n
  "指定した個数だけpopする"
  [n v]
  (if (zero? n)
    v
    (recur (dec n) (pop v))))

(defn peek-n
  "指定した個数だけpeekする"
  [n v]
  (if (zero? n)
    []
    (conj (peek-n (dec n) (pop v))
          (peek v))))

(defn sorted-card?
  "連番のカードか？"
  [cs]
  (if (empty? cs)
    false
    (loop [c1 (first cs), c2 (second cs), r (drop 2 cs)]
      (if (nil? c2)
        true
        (if (= 1 (- (:rank c1) (:rank c2)) )
          (recur c2 (first r) (rest r))
          false)))))

(defn movable?
  "山からn枚を移動することは合法か？"
  [deck from to n]
  (let [fs (nth (:stacks deck) from)
        ts (nth (:stacks deck) to)]
    (if (< (count fs) n)
      false
      (let [move-card (peek-n n fs)]
        (and (every? open-card? move-card)
             (or (empty? ts)
                 (sorted-card? (concat [(last ts)] move-card))))))))

(defn move-card
  "n枚のカードを山同士で移動する"
  [deck from to n]
  (let [stacks (:stacks deck)
        move-card (peek-n n (nth stacks from))
        new-from-stk (open-stack-top (pop-n n (nth stacks from)))
        new-to-stk (vec (concat (nth stacks to) move-card))]
    (-> deck
        (assoc-in [:stacks from] new-from-stk)
        (assoc-in [:stacks to] new-to-stk))))

(defn drawable?
  "札を山札に配布することは可能か？"
  [{stk :stacks}]
  (every? #(not (empty? %)) stk))

(defn move-draw
  "山札から10枚引く"
  [deck]
  (let [stacks (:stacks deck)
        draw (:draw deck)
        card-draw (map open-card (take 10 draw))
        rest-draw (drop 10 draw)]
    (if (not-empty card-draw)
      (assoc deck
             :stacks (map (fn [stk c] (conj stk c))
                          stacks
                          card-draw)
             :draw rest-draw)
      deck)))

(defn full-card?
  "カードが揃っているか"
  [stk]
  (if (< (count stk) 13)
    false
    (let [cards (peek-n 13 stk)]
      (and (= 13 (:rank (first cards)))
           (sorted-card? cards)))))

(defn remove-sorted-card
  "揃ったカードをデッキから除外する"
  [deck]
  (assoc deck
         :stacks
         (mapv #(if (full-card? %)
                  (open-stack-top (pop-n 13 %))
                  %)
               (:stacks deck))))

(defn move
  "札の移動"
  [draw? from to n]
  (let [deck (current-deck)]
    (if draw?
      (if (drawable? deck)
        (push-history (remove-sorted-card (move-draw deck)))
        (println "!!! 空の山があると札を配布できません !!!"))
      (if (movable? deck from to n)
        (push-history (remove-sorted-card (move-card deck from to n)))
        (printf "!!! %d枚を%d列から%d列に移動することは出来ません !!!\n" n from to)))))

(defn move-undo
  "一手戻す"
  []
  (pop-history))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; display

(defn vec->str [v]
  (reduce str
          (interpose " " v)))

(defn card->str
  "表示用に文字列にする"
  [card]
  (if (open-card? card)
    (condp = (:rank card)
       1 "A"
      11 "J"
      12 "Q"
      13 "K"
      (str (:rank card)))
    "*"))

(defn stack->str
  "カードの山を出力用の文字列にする"
  [stk]
  (vec->str (map card->str stk)))

(defn display
  "ゲームの状態を出力する"
  [deck]
  (let [{stacks :stacks, draw :draw} deck]
    (loop [st (first stacks), r (rest stacks), idx 0]
      (printf "%d: %s\n" idx (stack->str st))
      (if (not (empty? r))
        (recur (first r) (rest r) (inc idx))))
    (println "draw: " (count draw))))

(defn display-current []
  (display (current-deck)))
