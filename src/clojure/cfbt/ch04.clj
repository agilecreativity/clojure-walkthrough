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

(def vampire-database
  {0 {:make-blood-pun? false, :has-pulse? true  :name "McFishwich"}
   1 {:make-blood-pun? false, :has-pulse? true  :name "McMacson"}
   2 {:make-blood-pun? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:make-blood-pun? true,  :has-pulse? true  :name "Mickey Mouse"}})

(defn vampire-related-details [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire? [record]
  (and (:make-blood-pun? record)
       (not (:has-pulse? record))
       record))

(defn identify-vampire [social-security-numbers]
  (first (filter vampire? (map vampire-related-details social-security-numbers))))

(time (vampire-related-details 0))
;; "Elapsed time: 1000.76577 msesc"
;; {:make-blood-pun? false, :has-pulse? true, :name "McFishwich"}

(time (def mapped-details (map vampire-related-details (range 0 1000000))))
;; "Elapsed time: 0.145619 msecs"

(time (first mapped-details)) ;; {:make-blood-pun? false, :has-pulse? true, :name "McFishwich"}

;; infinite sequences
(concat (take 8 (repeat "na")) ["Batman!"]) ;;("na" "na" "na" "na" "na" "na" "na" "na" "Batman!")

(take 3 (repeatedly (fn [] (rand-int 10)))) ;; (1 8 0)

(defn even-numbers
  ([]  (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers)) ;;(0 2 4 6 8 10 12 14 16 18)

(cons 0 '(2 4 6)) ;; (0 2 4 6)

;; The collection abstraction
(empty? []) ;; true

(empty? ["no"]) ;; false

(map identity {:sunlight-reaction "Glitter!"}) ;; ([:sunlight-reaction "Glitter!"])

(into {} (map identity {:sunlight-reaction "Glitter!"})) ;; {:sunlight-reaction "Glitter!"}

(map identity [:garlic-clove :garlic-clove]) ;; (:garlic-clove :garlic-clove)

(into #{} (map identity [:garlic-clove :garlic-clove])) ;; #{:garlic-clove}

(into {:facorite-emotion "gloomy"} [[:sunlight-reaction "Glitter!"]])
;; {:facorite-emotion "gloomy", :sunlight-reaction "Glitter!"}

(into ["cherry"] '("pine" "spruce"))
;; ["cherry" "pine" "spruce"]

(into {:favorite-animal "kitty"} {:least-favorite-smell "dog"
                                  :relationship-with-teenager "creepy"})
;; {:favorite-animal "kitty", :least-favorite-smell "dog", :relationship-with-teenager "creepy"}

;; conj
(conj [0] [1]) ;; [0 [1]]

(into [0] [1]) ;; [0 1]

(conj [0] 1) ;; [0 1]

(conj [0] 1 2 3 4 5) ;; [0 1 2 3 4 5]

(conj {:time "midnight"} {:place "ye olde cemetarium"})
;; {:time "midnight", :place "ye olde cemetarium"}

;; define conj interm of into
(defn my-conj [target & additions]
  (into target additions))

(my-conj [0] 1 2 3 4 5) ;; [0 1 2 3 4 5]

;; functions
(max 0 2 3 1 5 6) ;; 6
(apply max [0 1 2 3 4]) ;; 4

(defn my-into
  [target additions]
  (apply conj target additions))

(my-into [] [0 1 2 3 4]) ;; [0 1 2 3 4]

;; partial
(def add10 (partial + 10))

(add10 3) ;; 13

(add10 5) ;; 15

(def add-missing-elements
  (partial conj ["water" "earth" "air"]))

(add-missing-elements "fire") ;; ["water" "earth" "air" "fire"]
(add-missing-elements "unotainium" "adamantium") ;; ["water" "earth" "air" "unotainium" "adamantium"]

(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(def add20 (my-partial + 20))
(add20 3) ;; 23

(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))
(warn "Red light ahead") ;; "red light ahead"

(def danger (partial lousy-logger :emergency))
(danger "You should stop now") ;; "YOU SHOULD STOP NOW"

;; Complement
(defn identity-humans
  [social-security-numbers]
  (filter #(not (vampire? %))
          (map vampire-related-details social-security-numbers)))

;; complement
(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))

(def my-pos? (complement neg?))

(my-pos? 1) ;; true
(my-pos? 0) ;; true
(my-pos? -1) ;; false

;; A Vampire Data Ananysis Program for FWPD (p 93): TBC
