(ns clojure-walkthrough.cjia.ch03-04
  (use clojure.pprint)
  (:gen-class))

(def MAX-CONNECTION 10)

(def RABBITMQ-CONNECTION)

(def ^:dynamic RABBITMQ-CONNECTION)

;; (binding [RABBITMQ-CONNECTION (new-connection)]
;;   (
;;    ;; do something here with RABBITMQ-CONNECTION
;;    ))

;; Special variables

(def ^:dynamic *db-host* "localhost")

(defn expense-report [start-date end-date]
  (println *db-host*))  ;; cand do real work

;; rebind once we are ready
(binding [*db-host* "production"]
  (expense-report "2010-01-01" "2010-01-07")) ;; Will print "production" to the console

;; Dynamic Scope

(def ^:dynamic *eval-me* 10)

(defn print-the-var [label]
  (println label *eval-me*))

(print-the-var "A:") ;; will print "A: 10" in the REPL

(binding [*eval-me* 20] ;; the first binding
  (print-the-var "B:")
  (binding [*eval-me* 30] ;; the 2nd binding
    (print-the-var "C:"))
  (print-the-var "D:"))

;; Result:
;; A: 10
;; B: 20
;; C: 30
;; D: 20

(print-the-var "E:")

;; Result:
;; E: 10

;; 3.5 - A high-order function for aspect-oriented logging
(defn ^:dynamic twice [x]
  (println "original function")
  (* 2 x))

(defn call-twice [y]
  (twice y))

(defn with-log [function-to-call log-statement]
  (fn [& args]
    (println log-statement)
    (apply function-to-call args)))

(call-twice 10) ;; the REPL print "original function" and return 20

(binding [twice (with-log twice "Calling the twice function")]
  (call-twice 20)) ;; 40
;; Console:
;; Calling the twice function
;; original function

(call-twice 30) ;; 60
;; Console:
;; original function

;; Laziness and Special Variables
(def ^:dynamic *factor* 10)

(defn multiply [x]
  (* x *factor*))

(map multiply [1 2 3 4 5]) ;; (10 20 30 40 50)

;; If we use binding call
(binding [*factor* 20]
  (map multiply [1 2 3 4 5])) ;; (10 20 30 40 50)
;; Call to map returns lazy sequence

;; To make this work we need to force non-lazy realization
(binding [*factor* 20]
  (doall (map multiply [1 2 3 4 5]))) ;; (20 40 60 80 100)

;; 3.4.2: The let form revisited

(let [x 10
      y 20]
  [x y]) ;; [10 20]

;; defined function locally inside lexical scope of let form
(defn upcased-name [names]
  (let [up-case (fn [name] (.toUpperCase name))]
    (map up-case names)))

(upcased-name ["foo" "bar" "baz"]) ;; ("FOO" "BAR" "BAZ")

;; Difference between let and binding forms
(def ^:dynamic *factor* 10)
(binding [*factor* 20]
  (println *factor*)
  (doall (map multiply [1 2 3 4 5]))) ;; (20 40 60 80 100)

;; using the let form
(let [*factor* 20]
  (println *factor*)
  (doall (map multiply [1 2 3 4 5]))) ;; (10 20 30 40 50)
;; print 20 five time and has no effect on the dynamic scope

;; 3.4.3 - Lexical closures
(defn create-scaler [scale]
  (fn [x]
    (* x scale)))

(def percent-scaler (create-scaler 100))

(percent-scaler 0.59) ; 59.0
