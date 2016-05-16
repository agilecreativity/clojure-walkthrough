;; See: https://clojuredocs.org/clojure.test/deftest
(ns cjapplied.ch08)
(use 'clojure.test)

(deftest addition
  (is (= 4 (+ 2 2)))
  (is (= 7 (+ 3 4))))

(deftest subtraction
  (is (= 1 (- 4 3)))
  (is (= 3 (- 7 4))))

;; composing tests
(deftest arithmetic
  (addition)
  (subtraction))

(deftest test-range
  (is (= '(0 1 2 3 4) (range 5))))

(run-tests 'cjapplied.ch08) 
;; {:test 6, :pass 9, :fail 0, :error 0, :type :summary}

(run-all-tests) ;; 
{:test 9, :pass 10, :fail 1, :error 1, :type :summary}
