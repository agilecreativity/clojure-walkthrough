(ns cjfn.07-control-flow
  (:require [clojure.java.io :as io]))

;; if-do
(if (even? 5)
  (do
    (println "even")
    true)
  (do (println "odd")
      false)) ;; false

;; if-let
(if-let [x (even? 3)]
  (str "x is :" x)
  (str "some odd value")) ;; "some odd value"

;; Empty list is truthy
(if ()
  "I am empty and is truthy!"
  "I am empty and is falsey") ;; "I am empty and is truthy!"

;; Note:
(filter even? [1 3 5]) ;; () ;; which is truthy!

(seq (filter even? [1 3 5])) ;; nil ;; which is falsey!
1
(defn show-evens [cols]
  (if-let [evens (seq (filter even? cols))]
    (str "The evens numbers are: " evens)
    (str "There were no evens in : " cols)))

;; if we have the condition
(show-evens [1 2 3 4 5 6 7 8 9 10]) ;; "The evens numbers are: (2 4 6 8 10)"

;; If we have no matching number
(show-evens '[1 3 5 7]) ;; "There were no evens in : [1 3 5 7]"

;; 07-09: cond
(defn what-am-i [x]
  (cond
    (< x 2) "x is less than 2"
    (< x 10) "x is less than 10"
    :else "x is greater than or equal to 10"))

(what-am-i 1) ;; "x is less than 2"
(what-am-i 3) ;; "x is less than 10"
(what-am-i 11);; "x is greater than or equal to 10"

;; 07-10: condp, shared predicate
(defn describe-number [x]
  (condp = x
    5 "x is 5"
    10 "x is 10"
    "x is neither 5 nor 10"))

(describe-number 10) ;; "x is 10"
(describe-number 5)  ;; "x is 5"
(describe-number 20) ;; "x is neither 5 nor 10"

;; 07-11: cases

(defn case-demo [x]
  (case x
    5 "x is 5"
    10 "x is 10"
    "x is neither 5 nor 10"))

(case-demo 10) ;;"x is 10"
(case-demo 5)  ;;"x is 5"
(case-demo 30) ;;"x is neither 5 nor 10"

;; 07-12: demo
(defn str-binary [n]
  (cond
    (= n 0) "zero"
    (= n 1) "one"
    :else "unknown"))

(str-binary 0) ;; "zero"
(str-binary 1) ;; "one"
(str-binary 3) ;; "unknown"

(defn str-binary-p [n]
  (condp = n
    0 "zero"
    1 "one"
    "unknown"))

(str-binary-p 0) ;; "zero"
(str-binary-p 1) ;; "one"
(str-binary-p 20);; "unknown"

(defn str-binary-case [n]
  (case n
    0 "zero"
    1 "one"
    "unknown"))

(str-binary-case 1)  ;; "one"
(str-binary-case 0)  ;; "zero"
(str-binary-case 30) ;; "unknown"

;; 07-14: doseq
(doseq [n (range 3)]
  ;; comes with side-effect
  (println n))

(doseq [x (range 3)
        y (range 5)]
  (println x y))

;; 07-15: dotimes
(dotimes [i 3]
  (println i)) ;; will print 0, 1, 2

;; 07-17: for for sequence permutation
(for [x [0 1]
      y [2 3]]
  [x y]) ;; ([0 2] [0 3] [1 2] [1 3])

;; 07-18: loop/recur
(loop [i 0]
  (if (< i 10)
    (recur (inc i))
    i)) ;; 10

;; 7-19: defn/recur
(defn increase [i]
  "Increment a number less than 10 to at most 10,
  otherwise just return that number"
  (if (< i 10)
    (recur (inc i))
    i))

(increase 1)  ;; 10
(increase 3)  ;; 10
(increase 20) ;; 20

;; 07-21: recur for recursively function
(defn factorial-with-problem
  "Factorial that will not work well with large number"
  ([n] (factorial 1 n))
  ([acc n]
   (if (zero? n)
     acc
     (factorial (* acc n) (dec n)))))

(factorial-with-problem 5) ;; 120

;; (factorial-with-problem 100) ;; => StackOverflow

;; Note: still have the problem with large number
;; See: http://noahlz.github.io/factorials/factorials.core.html

;; (defn factorial-ok
;;   "Factorial that work with larger number"
;;   ([n] (factorial-ok 1 n))
;;   ([acc n]
;;    (if (zero? n)
;;      acc
;;      (recur (* acc n) (dec n)))))
;; (factorial-ok 100)

;; (defn factorial-with-recur [n]
;;   (loop [current n
;;          next (dec current)
;;          total 1]
;;     (if (> current 1)
;;        (recur next (dec next) (* total current))
;;        total)))

;; (factorial-with-recur 5)  ;;
;; (factorial-with-recur 20) ;; 2432902008176640000

;; 07-22: Exception handling
(defn try-exception [x y]
  (try
    (/ x y)
    (catch ArithmeticException e
      "devided by zero!")
    (finally
      (println "Cleanup.."))))

(try-exception 1 2) ;; 1/2
(try-exception 1 0) ;; "devided by zero!"

;; 07-23: thowing exceptions
(try
  (throw (Exception. "Something went wrong"))
  (catch Exception e (.getMessage e))) ;; "Something went wrong"

;; 07-24: with-open
(require '[clojure.java.io :as io])

;; resource will be closed automatically
(with-open [f (io/writer "/tmp/new")]
  (.write f "some-text"))
