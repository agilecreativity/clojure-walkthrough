(ns misc.others)

;; https://clojuredocs.org/clojure.core/->

;; Thread last examples
(->> (range)
     (map #(* % %))
     (filter even?)
     (take 10)) ;; (0 4 16 36 64 100 144 196 256 324)

(macroexpand-1 '(->> (range)
                     (map #(* % %))
                     (filter even?)
                     (take 10)))
;; (take 10 (filter even? (map (fn* [p1__26348#] (* p1__26348# p1__26348#)) (range))))

(macroexpand-1 '(->> (range)
                     (map #(* % %))
                     (filter even?)
                     (take 10)
                     (reduce +)))

;; (reduce + (take 10 (filter even? (map (fn* [p1__26316#] (* p1__26316# p1__26316#)) (range)))))
;; Get te sum of the first 10 even squares
(->> (range)
     (map #(* % %))
     (filter even?)
     (take 10)
     (reduce +)) ;; 1140

;; Simple

(macroexpand '(->> 5 (+ 3) (/ 2) (- 1))) ;; (- 1 (/ 2 (+ 3 5)))

(->> 5 (+ 3) (/ 2) (- 1))  ;; 3/4

;; Thread first
(macroexpand '(->  0 (+ 1) (+ 2) (+ 3))) ;; (+ (+ (+ 0 1) 2) 3)
(macroexpand '(->> 0 (+ 1) (+ 2) (+ 3))) ;; (+ 3 (+ 2 (+ 1 0)))

(-> 0 (+ 1) (* 2) (/ 3)) ;; 2/3
(macroexpand '(-> 0 (+ 1) (* 2) (/ 3))) ;; (/ (* (+ 0 1) 2) 3)

(->> 0 (+ 1) (* 2) (/ 3)) ;; 3/2
(macroexpand '(->> 0 (+ 1) (* 2) (/ 3))) ;; (/ 3 (* 2 (+ 1 0)))

;; Good examples
(->> [1 2 3 4 5]
     ((fn [coll] (map inc coll)))
     (apply +)) ;; 20

;; More good examples
;; without using the thread-first (difficult to read)
(first (.split (.replace (.toUpperCase "a b c d e") "A" "X") " "))

;; much more easy to read
(-> "a b c d e" .toUpperCase (.replace "A" "X") (.split " ") first) ;; "X"
(macroexpand-1 '(-> "a b c d e" .toUpperCase (.replace "A" "X") (.split " ") first))
;; (first (.split (.replace (.toUpperCase "a b c d e") "A" "X") " "))

;; Useful for pulling values out of deeply-nested data-structures:
(def person
  {:name "Mark Volkmann"
   :address {:street "644 Glen Summit"
             :city "St. Charles"
             :state "Missouri"
             :zip 63304}
   :employer {:name "Object Consulting, Inc."
              :address {:street "12140 Woodcrest Dr."
                        :city "Cleve Coer"
                        :state "Missouri"
                        :zip 63141}}})

;; Use thread first to get the information that we need
(-> person :employer :address :city) ;; "Cleve Coer"

;; Without the use of thread-first
(:city (:address (:employer person))) ;; "Cleve Coer"
