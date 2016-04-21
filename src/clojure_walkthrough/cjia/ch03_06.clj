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

;; 3.6.2: Map bindings

(def salary-details {:first-name "Barry"
                     :last-name  "Gibb"
                     :salary     1.0M })

(defn describe-salary-2 [{first :first-name
                          last :last-name
                          annual :salary}]
  (str first " " last " earns " annual))

(describe-salary-2 salary-details) ;; "Barry Gibb earns 1.0"

;; Use of 'or' to handle optional values
(defn describe-salary-3 [{first  :first-name
                          last   :last-name
                          annual :salary
                          bonus  :bonus-percentage :or {bonus 5}}]
  (str first " " last " earns " annual " with a " bonus " percent bonus"))

(describe-salary-3 salary-details) ;; "Barry Gibb"Pascal Dylan earns 85000 with a 20 percent bonus" earns 1.0 with a 5 percent bonus"  

;; When call with all arguments will work normally

(def a-user {:first-name       "Pascal"
             :last-name        "Dylan"
             :salary           85000
             :bonus-percentage 20})

(describe-salary-3 a-user) ;; "Pascal Dylan earns 85000 with a 20 percent bonus"

(def another-user {:first-name "Basic"
                   :last-name  "Groovy"
                   :salary     7000})

;; Note: the default value is used if the bonus is missing
(describe-salary-3 another-user) ;; "Basic Groovy earns 7000 with a 5 percent bonus"

;; Use of :as to bind the complete hash map option to a name.
(defn describe-person [{first :first-name
                        last :last-name
                        bonus :bonus-percentage
                        :or {bonus 5}
                        :as p}]
  (str "Info about " first " " last " is :" p ", bonus is :" bonus " percent"))

(def third-user {:first-name "Lambda"
                 :last-name  "Curry"
                 :salary     95000})

(describe-person third-user) ;; "Info about Lambda Curry is :{:first-name \"Lambda\", :last-name \"Curry\", :salary 95000}, bonus is :5 percent"

;; Use of :keys
(defn greet-user [{:keys [first-name last-name]}]
  (str "Welcome, " first-name " " last-name))

(def roger {:first-name "Roger"
            :last-name  "Rabbit"
            :salary     6500})

(greet-user roger) ;; "Welcome, Roger Rabbit"
