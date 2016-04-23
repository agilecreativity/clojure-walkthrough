;; Note: replace ch03-xx the right number
(ns clojure-walkthrough.cjia.ch05-xx
  (:require [clojure.data.json :as json]
            [clojure.xml :as xml-core])
  (:gen-class))

;; import java
(import 'java.util.Date 'java.text.SimpleDateFormat)

;; import more than one at a time from the same package
;; in this case from 'java.util'
(import '[java.util Date Set]) ;; java.util.Set

;; The recommended way is to to import into a namespace like
(ns clojure-walkthrough.cjia.ch05-xx
  (:import (java.util Set Date))
  (:import (java.text SimpleDateFormat)))

;; Now we can use them
(def sdf (new SimpleDateFormat "yyyy-MM-dd"))

;; Or alternatively, Clojure allow us to write something like
(def sdf (SimpleDateFormat. "yyyy-MM-dd"))

(defn date-from-date-string [date-string]
  (let [sdf (SimpleDateFormat. "yyyy-MM-dd")]
    (.parse sdf date-string)))

(date-from-date-string "2016-01-30") ;; #inst "2016-01-29T13:00:00.000-00:00"

;; Static Methods
(Long/parseLong "12321") ;; 12321

;; calling with (Classname/staticMethod args*)

;; Static Fields
(import '(java.util Calendar)) ;; java.util.Calendar

(Calendar/JANUARY) ;; 0

(Calendar/FEBRUARY) ;; 1

;; 5.1.4 Macros and the dot special form

(. System getenv "PATH") 
(. System (getenv "PATH"))

;; Use of Java instance object
(import '(java.util Random)) ;; java.util.Random

(def rnd (Random.)) ;; #'clojure-walkthrough.cjia.ch05-xx/rnd

(. rnd (nextInt 10)) ;; 3 ;; result will vary
(. rnd (nextInt 10)) ;; 6 ;; result will vary
