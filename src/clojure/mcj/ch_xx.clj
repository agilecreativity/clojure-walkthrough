(ns mcj.ch_xx
  (require [clojure.core.match :as m]
           [defun :as f]))

;; Pattern matching using the defun macro
(defn xor [x y]
  (m/match [x y]
    [true true] false
    [false true] true
    [true false] true
    [false false] false))

(xor (= 2 3) (= 3 3)) ; true
;(xor 0 0) ;; IllegalArgumentException: No matching clause

(f/defun fibo
  ([0] 0N)
  ([1] 1N)
  ([n] (+ (fibo (- n 1))
          (fibo (- n 2)))))
