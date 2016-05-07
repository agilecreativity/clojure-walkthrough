(ns cfbt.ch10)

(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorate 0}))

@fred ;; {:cuddle-hunger-level 0, :percent-deteriorate 0}

(let [zombie-state @fred]
  (if (>= (:percent-deteriorate zombie-state) 50)
    (future (println (:percent-deteriorate zombie-state)))))

(swap! fred
       (fn [current-state]
         (merge-with + current-state {:current-hunger-level 1})))
;; {:cuddle-hunger-level 0, :percent-deteriorate 0, :current-hunger-level 3}
@fred
;; {:cuddle-hunger-level 0, :percent-deteriorate 0, :current-hunger-level 3}
(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 2
                                      :percent-deteriorated 1})))

@fred

(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1
                                      :percent-deteriorated 1})))

(defn increase-cuddle-hunger-level
  [zombie-state increase-by]
  (merge-with + zombie-state {:cuddle-hunger-level increase-by}))

;; No update will be applied without the use of swap
(increase-cuddle-hunger-level @fred 10)

@fred

(swap! fred increase-cuddle-hunger-level 10)

@fred

;; use Clojure built-in
(update-in {:a {:b 3}} [:a :b] inc)
;; {:a {:b 4}}

(update-in {:a {:b 3}} [:a :b] + 10)
;; {:a {:b 13}}

(swap! fred update-in [:cuddle-hunger-level] + 10)

(let [num (atom 1)
      s1 num]
  (swap! num inc)
  (println "State 1:" s1)
  (println "Current state:" @num))

(reset! fred {:cuddle-hungerlevel 0
              :percent-deteriorated 0})
;; {:cuddle-hungerlevel 0, :percent-deteriorated 0}

;; Watches and Validators ::
(defn shuffle-speed
  [zombie]
  (* (:cuddle-hunger-level zombie)
     (- 100 (:percent-deteriorated zombie))))

(defn shuffle-alert
  [key watched old-state new-state]
  (let [sph (shuffle-speed new-state)]
    (if (> sph 5000)
      (do
        (println "Run, you fool!")
        (println "The zombie's SPH is now " sph)
        (println "This message brought to you courtesy of " key))
      (do
        (println "All's well with " key)
        (println "Cuddle hunger: " (:cuddle-hunger-level new-state))
        (println "Percent deteriorated: " (:percent-deteriorated new-state))
        (println "SPH: " sph)))))

(reset! fred {:cuddle-hunger-level 22
              :percent-deteriorated 2})
;; {:cuddle-hunger-level 22, :percent-deteriorated 2}
(add-watch fred :fred-shuffle-alert shuffle-alert)
(swap! fred update-in [:percent-deteriorated] + 2)
(swap! fred update-in [:cuddle-hunger-level] + 30)

;; Validators ::

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (and (>= percent-deteriorated 0)
       (<= percent-deteriorated 100)))

;; Use validator at creation time
(def boby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
    :validator percent-deteriorated-validator))

;;(swap! boby update-in [:percent-deteriorated] + 200)
;; => Throw "Invalid reference state!"

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (or (and (>= percent-deteriorated 0)
           (<= percent-deteriorated 100))
      (throw (IllegalStateException. "That's not mathly"))))

(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator))

(swap! bobby update-in [:percent-deteriorated] + 200)
