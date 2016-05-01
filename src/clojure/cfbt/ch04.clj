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
