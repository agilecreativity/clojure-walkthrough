;; Destructuring
(ns clojure-walkthrough.cjia.ch03-06
  (:require [clojure.data.json :as json]
            [clojure.xml :as xml-core])
  (:gen-class))

(defn describe-salary [person]
  (let [first (:first-name person)
        last  (:last-name  person)
        annual (:salary person)]
    (println first last "earns" annual)))

;; with destructuring
(defn describe-salary-2 [{first :first-name
                          last :last-name
                          annual :salary}]
  (println first last "earns" annual))

;; 3.6.1: vector bindings

(defn show-amounts [[amount-1 amount-2]]
  (str "amounts are:" amount-1 amount-2))

(show-amounts [10.95 31.45]) ;; "amounts are:10.9531.45"

;; Using & and :as
(defn show-amounts-multiple [[amount-1 amount-2 & remaining]]
  (str "Amounts are:" amount-1 "," amount-2 "and" remaining))

(show-amounts-multiple [10.5 31.45 22.36 2.95]) ;; "Amounts are:10.5,31.45and(22.36 2.95)"

(defn show-all-amounts [[amount-1 amount-2 & remaining :as all]]
  (println "Amount are:" amount-1 "," amount-2 "and" remaining)
  (println "Also, all the amounts are:" all))

(show-all-amounts [10.5 31.45 22.36 2.95]) ;; =>
;; Amount are: 10.5 , 31.45 and (22.36 2.95)
;; Also, all the amounts are: [10.5 31.45 22.36 2.95]

;; Nested Vectors
;; Take note: we only interest in the first item, ignore the rest
(defn print-first-category [[[category amount] & _ ]]
  (println "First category was:" category)
  (println "First amount was:" amount))

;; setup some data
(def expenses [[:books 49.95] [:coffee 4.95] [:caltrain 2.25]])

(print-first-category expenses) ;; =>
;; First category was: :books
;; First amount was: 49.95

