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

;; Using refs
(def state (ref 0))
@state ;; 0

(dosync (ref-set state 1)) ;; 1
@state ;; 1

(dosync (alter state + 2)) ;; 3

(dosync (commute state + 2)) ;; 5

(def r (ref 1 :validator pos?))

;(dosync (alter r (fn [_] -1))) ;; IllegalStateException

;; But this will return as it pass the validation
(dosync (alter r (fn [_] 20))) ;; 20

;; Dining philosopher problem

(defn make-fork []
  (ref true))

(defn make-philosopher [name forks food]
  (ref {:name name
        :forks forks
        :eating? false
        :food food}))

(defn has-forks? [p]
  (every? true? (map ensure (:forks @p))))

(defn update-forks [p]
  (doseq [f (:forks @p)]
    (commute f not))
  p)

(defn start-eating [p]
  (dosync
   (when (has-forks? p)
     (update-forks p)
     (commute p assoc :eating? true)
     (commute p update-in [:food] dec))))

(defn stop-eating [p]
  (dosync
   (when (:eating? @p)
     (commute p assoc :eating? false)
     (update-forks p))))

(defn dine
  [p retry-ms max-eat-ms max-think-ms]
  (dosync
   (when (:eating? @p)
     (do
       (Thread/sleep (rand-int max-eat-ms))
       (stop-eating p)
       (Thread/sleep (rand-int max-think-ms)))
     (Thread/sleep retry-ms))))

(defn init-forks [nf]
  (repeatedly nf #(make-fork)))

(defn init-philosophers [np food forks init-fn]
  (let [p-range (range np)
        p-names (map #(str "Philosopher " (inc %))
                     p-range)
        p-forks (map #(vector (nth forks %)
                              (nth forks (-> % inc (mod np))))
                     p-range)
        p-food (cycle [food])]
    (map init-fn p-names p-forks p-food)))

(defn check-philosophers [philosophers forks]
  (doseq [i (range (count philosophers))]
    (println (str "Fork:\t\t\t available=" @(nth forks i)))
    (if-let [p @(nth philosophers i)]
      (println (str (:name p)
                    ":\t\t eating=" (:eating? p)
                    " food=" (:food p))))))

(defn dine-philosophers [philosophers]
  (doall (for [p philosophers]
           (future (dine p 10 100 100)))))

(def all-forks (init-forks 5))

(def all-philosophers
  (init-philosophers 5 1000 all-forks make-philosopher))

(check-philosophers all-philosophers all-forks)

(def philosophers-futures (dine-philosophers all-philosophers))

(check-philosophers all-philosophers all-forks)

;; Pause the simulation
(map future-cancel philosophers-futures)

;; Using atoms:

(def state (atom 0))

@state ;; 0

(reset! state 1) ;; 1

@state ;; 1

(swap! state + 2) ;; 3

;; Using add-watch function (contain error in the book)
(defn make-state-with-watch []
  (let [state (atom 0)
        state-is-changed? (atom false)
        watch-fn (fn [key r old-value new-value]
                   (swap! state-is-changed? (fn [_] true)))]
    (add-watch state nil watch-fn)
    [state
     state-is-changed?]))

(def s (make-state-with-watch))

@(nth s 1) ;; false

(swap! (nth s 0) inc) ;; 1

@(nth s 1) ;; true

;; Using agents

