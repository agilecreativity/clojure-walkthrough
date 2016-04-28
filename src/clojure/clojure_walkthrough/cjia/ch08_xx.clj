(ns clojure-walkthrough.cjia.ch08-xx)

;; More on functional programming
(defn square [x]
  (* x x))

;; Idiomatic equivalent to empty list
(defn square-all [numbers]
  (if (empty? numbers)
    nil
    (cons (square (first numbers))
          (square-all (rest numbers)))))

(square-all [1 2 3 4 5 6]) ;; (1 4 9 16 25 36)

(defn cube [x]
  (* x x x))

(defn cube-all [numbers]
  (if (empty? numbers)
    ()
    (cons (cube (first numbers))
          (cube-all (rest numbers)))))

;; Again easy to test
(cube-all [1 2 3 4 5 6]) ;; (1 8 27 64 125 216)

(defn do-to-all [f numbers]
  (if (empty? numbers)
    ()
    (cons (f (first numbers))
          (do-to-all f (rest numbers)))))

(do-to-all cube [1 2 3 4 5 6]) ;; (1 8 27 64 125 216)

(do-to-all square [1 2 3 4 5 6]) ;; (1 4 9 16 25 36)

;; if it is too big, it will blow up!
;(do-to-all square (range 10000)) ;; StackOverflow

(defn do-to-all [f numbers]
  (lazy-seq
   (if (empty? numbers)
     ()
     (cons (f (first numbers))
           (do-to-all f (rest numbers))))))

(do-to-all square (range 10000)) ;; No StackOverflow

(take 10 (drop 10000 (do-to-all square (range 11000)))) ;; (100000000 100020001 100040004 100060009 100080016 100100025 100120036 100140049 100160064 100180081)

(take 10 (drop 10000 (do-to-all square (range 11000)))) ;; (100000000 100020001 100040004 100060009 100080016 100100025 100120036 100140049 100160064 100180081)

;; 8.1.2: reduce lists of things
(defn total-of [numbers]
  (loop [nums numbers sum 0]
    (if (empty? nums)
      sum
      (recur (rest nums) (+ sum (first nums))))))

(total-of [5 7 9 3 4 1 2 8]) ;; 39

(defn larger-of [x y]
  (if (> x y) x y))

(larger-of 20 40) ;; 40
(larger-of 30 20) ;; 30

(defn largest-of [numbers]
  (loop [l numbers candidate (first numbers)]
    (if (empty? l)
      candidate
      (recur (rest l) (larger-of candidate (first l))))))

(largest-of [5 7 9 3 4 1 2 8]) ;; 9

(largest-of []) ;; nil

(defn compute-across [func elements value]
  (if (empty? elements)
    value
    (recur func (rest elements) (func value (first elements)))))

(defn total-of [numbers]
  (compute-across + numbers 0))

(defn largest-of [numbers]
  (compute-across larger-of numbers (first numbers)))

(defn all-greater-than [theshold numbers]
  (compute-across #(if (> %2 threshold) (conj %1 %2) %1) numbers [])))

(total-of [5 7 9 3 4 1 2 8]) ;; 39
(largest-of [5 7 9 3 4 1 2 8]) ;; 9

(defn all-greater-than [threshold numbers]
  (compute-across #(if (> %2 threshold) (conj %1 %2) %1) numbers []))

(all-greater-than 5 [5 7 9 3 4 1 2 8]) ;; [7 9 8]

;; Equivalent of `compute-across` using `reduce`
(defn all-greater-than [threshold numbers]
  (reduce #(if (> %2 threshold) (conj %1 %2) %1) [] numbers))

(all-greater-than 5 [5 7 9 3 4 1 2 8]) ;; [7 9 8]

;; 8.1.3: filtering lists of things (TBC)
