(ns cjfn.09-sequences)

;; Make sure that we don't print too much in the REPL
(set! *print-length* 10)

;; 09-09: creating a sequence

;; Note: this is not a list
(seq [1 2 3]) ;; (1 2 3)

(type (seq [1 2 3])) ;; clojure.lang.PersistentVector$ChunkedSeq
(type [1 2 3])  ;; clojure.lang.PersistentVector
(type '(1 2 3)) ;; clojure.lang.PersistentList

(range) ;; (0 1 2 3 ... infinite)
(range 3) ;; (0 1 2)

;; from 1 to 10 step 2
(range 1 10 2) ;; (1 3 5 7 9)

;; Calculate the power of two
(iterate #(* 2 %) 2) ;; (2 4 8 16 ... infinite)

;; Calculate the power of two, look at the first 8 result
(take 8 (iterate #(* 2 %) 2)) ;;(2 4 8 16 32 64 128 256)

(re-seq #"[aeiou]" "Clojure") ;; ("o" "u" "e")

;; 09-10: seq-in, seq-out
(take 3 (range)) ;; (0 1 2)

(drop 3 (range)) ;; (3 4 5 ... infinite)

(map #(* % %) [0 1 2 3 4 5]) ;; (0 1 4 9 16 25)

(take 10 (filter even? (range))) ;; (0 2 4 6 8 10 12 14 16 18)

;; Add the "," between item
(interpose "," (range 3)) ;; (0 "," 1 "," 2)

(apply str (interpose "," (range 3))) ;; "0,1,2"

;; 09-11: using a seq
(= '(0 1 2 3) (range 4)) ;; true

(reduce + '(0 1 2 3)) ;; 6

(reduce + (range 4)) ;; 6

;; with initial value
(reduce + 10 (range 4)) ;; 16

;; Note: order is not guarantee
(into #{} "hello") ;; #{\e \h \l \o}

;; Convert to map
(into {} [[:x 1] [:y 2]]) ;; {:x 1, :y 2}

;; nested vector to map
(into {} [[[:x 1] [:y 2]]]) ;; {[:x 1] [:y 2]}

(some {2 :b 3 :c} [1 nil 2 3]) ;; :b

;; The  fibonaci sequence
(take 10 (iterate (fn [[a b]]
                    [b (+ a b)])
                  [0 1]))
;; ([0 1] [1 1] [1 2] [2 3] [3 5] [5 8] [8 13] [13 21] [21 34] [34 55])

(def fib-seq (map first
                  (iterate (fn [[a b]]
                             [b (+ a b)])
                           [0 1])))

fib-seq ;; (0 1 1 2 3 5 8 13 21 34 ...)

(take 5 fib-seq) ;; (0 1 1 2 3)

(drop 5 fib-seq) ;; (5 8 13 21 34 55 89 144 233 377 ...)

(map inc (take 5 fib-seq)) ;; (1 2 2 3 4)
