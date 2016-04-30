(ns clojure-walkthrough.cjia.ch09-xx
  (:import [java.text SimpleDateFormat]))

(defn new-expense [date-string dollars cents category merchant-name]
  {:date (.parse (SimpleDateFormat. "yyyy-MM-dd") date-string)
   :amount-dollars dollars
   :amount-cents cents
   :category category
   :merchant-name merchant-name})

(defn total-cents [e]
  (-> (:amount-dollars e)
      (* 10)
      (+ (:amount-cents e))))

;; Adding some functions
(defn total-amount
  ([expenses-list]
   (total-amount (constantly true) expenses-list))
  ([pred expenses-list]
   (->> expenses-list
        (filter pred)
        (map total-cents)
        (apply +))))

(defn is-category? [e some-category]
  (= (:category e) some-category))

(defn category-is [category]
  #(is-category? % category))

;; Let's test it in the new namespaces
(ns clojure-walkthrough.cjia.ch09-xx-test
  (:require [clojure-walkthrough.cjia.ch09-xx :refer :all]
            [clojure.test :refer :all]))

;; (defn clj-expenses [(new-expense "2009-8-20" 21 95 "books" "Amazon.com")
;;                     (new-expense "2009-8-21" 72 43 "food"  "Mollie-Stones")
;;                     (new-expense "2009-8-22" 315 71 "car-rental" "Avis")
;;                     (new-expense "2009-8-23" 15 68  "books" "Borders")])
