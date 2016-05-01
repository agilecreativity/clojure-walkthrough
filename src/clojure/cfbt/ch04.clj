(ns clojure.cfbt.ch04)

(defn titleize [topic]
  (str topic " for the Brave and True"))

(map titleize ["Hamsters" "Ragnarok"]) ;; ("Hamsters for the Brave and True" "Ragnarok for the Brave and True")

(map titleize '("Hamsters" "Ragnarok")) ;; ("Hamsters for the Brave and True" "Ragnarok for the Brave and True")

(map titleize #{"Elbows" "Soap Carving"}) ;; ("Elbows for the Brave and True" "Soap Carving for the Brave and True")

(map titleize (second %)) {:uncomfortable-thing "Winking"} ;; {:uncomfortable-thing "Winking"}

;; First, rest, and cons
(seq '(1 2 3)) ;; (1 2 3)

(seq [1 2 3]) ;; (1 2 3)

(seq #{1 2 3}) ;; (1 3 2)

(seq {:name "Bill Compton" :occupation "Dead money guy"}) ;; ([:name "Bill Compton"] [:occupation "Dead money guy"])

(seq {:a 1 :b 2 :c 3})  ;; ([:a 1] [:b 2] [:c 3])

(into {} (seq {:a 1 :b 2 :c 3})) ;; {:a 1, :b 2, :c 3}

(map inc [1 2 3 4]) ;; (2 3 4 5)

(map str ["a" "b" "c"] ["A" "B" "C"]) ;; ("aA" "bB" "cC")

(list (str "a" "A") (str "b" "B") (str "c" "C")) ;; ("aA" "bB" "cC")

(def sum #(reduce + %))

(def avg #(/ (sum %) (count %)))

(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(stats [3 4 10]) ;; (17 3 17/3)

(stats [80 1 44 13 6]) ;; (144 5 144/5)

(def identities
  [{:alias "Batman"       :real "Bruce Wayne"}
   {:alias "Spider-Man"   :real "Peter Parker"}
   {:alias "Santa"        :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities) ;; ("Bruce Wayne" "Peter Parker" "Your mom" "Your dad")

;; reduce
(reduce (fn [new-map [key value]]
          (assoc new-map key (inc value)))
        {}
        {:max 30 :min 10}) ;; {:max 31, :min 11}

;; Use to filter out the values
(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1
         :critter 3.9}) ;; {:human 4.1}

; take, drop, take-while, and drop-while

(take 3 [1 2 3 4 5 6 7 8 9 10]) ;; (1 2 3)

(drop 3 [1 2 3 4 5 6 7 8 9 10]) ;; (4 5 6 7 8 9 10)

(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

(take-while #(< (:month %) 3) food-journal) ;; ({:month 1, :day 1, :human 5.3, :critter 2.3} {:month 1, :day 2, :human 5.1, :critter 2.0} {:month 2, :day 1, :human 4.9, :critter 2.1} {:month 2, :day 2, :human 5.0, :critter 2.5})

(drop-while #(< (:month %) 3) food-journal) ;; ({:month 3, :day 1, :human 4.2, :critter 3.3} {:month 3, :day 2, :human 4.0, :critter 3.8} {:month 4, :day 1, :human 3.7, :critter 3.9} {:month 4, :day 2, :human 3.7, :critter 3.6})

(take-while #(< (:month %) 4)
            (drop-while #(< (:month %) 2) food-journal)) ;; ({:month 2, :day 1, :human 4.9, :critter 2.1} {:month 2, :day 2, :human 5.0, :critter 2.5} {:month 3, :day 1, :human 4.2, :critter 3.3} {:month 3, :day 2, :human 4.0, :critter 3.8})

;; Filter and some

(filter #(< (:human %) 5) food-journal) ;; ({:month 2, :day 1, :human 4.9, :critter 2.1} {:month 3, :day 1, :human 4.2, :critter 3.3} {:month 3, :day 2, :human 4.0, :critter 3.8} {:month 4, :day 1, :human 3.7, :critter 3.9} {:month 4, :day 2, :human 3.7, :critter 3.6})

(filter #(< (:month %) 3) food-journal) ;; ({:month 1, :day 1, :human 5.3, :critter 2.3} {:month 1, :day 2, :human 5.1, :critter 2.0} {:month 2, :day 1, :human 4.9, :critter 2.1} {:month 2, :day 2, :human 5.0, :critter 2.5})

(some #(> (:critter %) 5) food-journal) ;; nil

(some #(> (:critter %) 3) food-journal) ;; true

(some #(and (> (:critter %) 3) %) food-journal) ;; {:month 3, :day 1, :human 4.2, :critter 3.3}

;; sort and sort-by
(sort [3 1 2]) ;; (1 2 3)

(sort-by count ["aaa" "c" "bb"]) ;; ("c" "bb" "aaa")

;; concat
(concat [1 2] [3 4]) ;;(1 2 3 4)

;; Lazy sequences
