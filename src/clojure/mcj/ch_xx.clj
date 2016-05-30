(ns mcj.ch_xx
  (require [clojure.core.match :as m]))

;; Pattern matching using the defun macro
(defn xor [x y]
  (m/match [x y]
    [true true] false
    [false true] true
    [true false] true
    [false false] false))

(xor (= 2 3) (= 3 3)) ; true
;(xor 0 0) ;; IllegalArgumentException: No matching clause

;; Using delays
(def delayed-1
  (delay
   (Thread/sleep 3000)
   (println "3 seconds later ..")
   1))

(realized? delayed-1) ;; false

;; after 3 seconds later
(realized? delayed-1) ;; true

;; Using futures and promises
(defn wait-3-seconds []
  (Thread/sleep 3000)
  (println)
  (println "3 seconds later..."))

(.start (Thread. wait-3-seconds))

(defn val-as-future [n secs]
  (future
    (Thread/sleep (* secs 1000))
    (println ("\n" (str secs " seconds later...")))
    n))

(def future-1 (val-as-future 1 3))

(realized? future-1) ;; true
(future-done? future-1) ;; true

(def future-10 (val-as-future 10 10))

;; cancel the future
(future-cancel future-10)

(future-cancelled? future-10) ;; true

;; @future-10 ;; will raise error

(def p (promise))

(deliver p 100) ;; #promise[{:status :ready, :val 100} 0x4cb2764d]

@p ;; 100

(deliver p 200)

@p ;; 100

(defn lock-for-2-seconds []
  (let [lock (Object. )
        task-1 (fn []
                 (future
                   (locking lock
                     (Thread/sleep 2000) ;; wait for 2 seconds
                     (println "Task 1 completed"))))
        task-2 (fn []
                 (future
                   (locking lock
                     (Thread/sleep 1000) ;; wait for 1 second
                     (println "Task 2 completed"))))]
    (task-1)
    (task-2)))

(lock-for-2-seconds)

;; Using vars

(def ^:dynamic *thread-local-state* [1 2 3])

(binding [*thread-local-state* [10 20]]
  (map #(* % %) *thread-local-state*)) ;; (100 400)

(map #(* % %) *thread-local-state*) ;; (1 4 9)

(with-bindings {#'*thread-local-state* [10 20]}
  (map #(* % %) *thread-local-state*)) ;; (100 400)

(with-bindings {(var *thread-local-state*) [10 20]}
  (map #(* % %) *thread-local-state*)) ;; (100 400)

(def ^:dynamic *unbound-var*) ;; #'mcj.ch_xx/*unbound-var*

(thread-bound? (var *unbound-var*)) ;; false

(binding [*unbound-var* 1]
  (thread-bound? (var *unbound-var*))) ;; true

;; Mutable variables using the with-local-vars form
(defn factorial [n]
  (with-local-vars [i n acc 1]
    (while (> @i 0)
      (var-set acc (* @acc @i))
      (var-set i (dec @i)))
    (var-get acc)))

(factorial 5) ;; 120
(factorial 20) ;; 2432902008176640000

;; Using refs: (TBC)
