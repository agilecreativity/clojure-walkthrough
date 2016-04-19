(ns clojure-walkthrough.jocj.ch03-04
  (use clojure.pprint)
  (:gen-class))

(def MAX-CONNECTION 10)

(def RABBITMQ-CONNECTION)

(def ^:dynamic RABBITMQ-CONNECTION)

(binding [RABBITMQ-CONNECTION (new-connection)]
  (
   ;; do something here with RABBITMQ-CONNECTION
   ))


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
