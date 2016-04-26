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

(Calendar/MARCH) ;; 2

;; 5.1.4 Macros and the dot special form

(. System getenv "PATH") 
(. System (getenv "PATH"))

;; Use of Java instance object
(import '(java.util Random)) ;; java.util.Random

(def rnd (Random.)) ;; #'clojure-walkthrough.cjia.ch05-xx/rnd

(. rnd (nextInt 10)) ;; 3 ;; result will vary
(. rnd (nextInt 10)) ;; 6 ;; result will vary

(import '(java.util Calendar TimeZone))

;; Compare this 
(. (. (Calendar/getInstance) getTimeZone) getDisplayName) ;; "Australian Eastern Standard Time (Victoria)"

;; To this
(.. (Calendar/getInstance) (getTimeZone) (getDisplayName)) ;; "Australian Eastern Standard Time (Victoria)"

;; This can be simplified to this
(.. (Calendar/getInstance) getTimeZone getDisplayName)     ;; "Australian Eastern Standard Time (Victoria)"

;; If using method signaturs that accepted arguments
(.. (Calendar/getInstance)
  getTimeZone
  (getDisplayName true TimeZone/SHORT)) ;; "AEDT"

;; Look at this example without the 'doto' macro
(import '(java.util Calendar))
(defn the-past-midnight-1 []
 (let [calendar-obj (Calendar/getInstance)]
  (.set calendar-obj Calendar/AM_PM Calendar/AM)
  (.set calendar-obj Calendar/HOUR 0)
  (.set calendar-obj Calendar/MINUTE 0)
  (.set calendar-obj Calendar/SECOND 0)
  (.set calendar-obj Calendar/MILLISECOND 0)
  (.getTime calendar-obj))) ;; #'clojure-walkthrough.cjia.ch05-xx/the-past-midnight-1

;; With the use of 'doto' macro
(import '(java.util Calendar))
(defn the-past-midnight-2 []
  (let [calendar-obj (Calendar/getInstance)]
    (doto calendar-obj
      (.set Calendar/AM_PM Calendar/AM)
      (.set Calendar/HOUR 0)
      (.set Calendar/MINUTE 0)
      (.set Calendar/SECOND 0)
      (.set Calendar/MILLISECOND 0))
    (.getTime calendar-obj)))

;; 5.1.5 some useful Clojure macros when working with Java
;; using `memfn` or 'member-as-function'
;; Suppose we have the following
(map (fn [x] (.getBytes x)) ["amit" "rob" "kyle"])

;; Can be simplified to 
(map #(.getBytes %) ["amit" "rob" "kyle"])

;; Using memfn
(memfn GetBytes) ;; #function[clojure-walkthrough.cjia.ch05-xx/eval20313/fn--20314]

(memfn ^String getBytes) ;; #function[clojure-walkthrough.cjia.ch05-xx/eval20323/fn--20324]

;; Using it in our example above
(map (memfn getBytes) ["amit" "rob" "kyle"]) ;; (#object["[B" 0x303317a5 "[B@303317a5"] #object["[B" 0x6a556a5b "[B@6a556a5b"] #object["[B" 0xdb9b2e7 "[B@db9b2e7"])N

;; Using memfn on String object
(.subSequence "Clojure" 2 5) ;; "oju"

;; The equivalent form with optional type hints is
((memfn ^String subSequence ^Long start ^Long end) "Clojure" 2 5) ;; "oju"

;; Java bean - Note the result is map which is much easier to deal with in some instance
(bean (Calendar/getInstance)) ;; {:weeksInWeekYear 53, :timeZone #object[sun.util.calendar.ZoneInfo 0x35e030c2 "sun.util.calendar.ZoneInfo[id=\"Australia/Melbourne\",offset=36000000,dstSavings=3600000,useDaylight=true,transitions=142,lastRule=java.util.SimpleTimeZone[id=Australia/Melbourne,offset=36000000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=3,startMonth=9,startDay=1,startDayOfWeek=1,startTime=7200000,startTimeMode=1,endMode=3,endMonth=3,endDay=1,endDayOfWeek=1,endTime=7200000,endTimeMode=1]]"], :weekDateSupported true, :weekYear 2016, :lenient true, :time #inst "2016-04-25T05:52:43.490-00:00", :timeInMillis 1461563563490, :class java.util.GregorianCalendar, :firstDayOfWeek 1, :gregorianChange #inst "1582-10-15T00:00:00.000-00:00", :minimalDaysInFirstWeek 1}

;; Array
(def tokens (.split "clojure.in.action" "\\.")) 

(alength tokens) ;; 3

(aget tokens 2) ;; "action"

(aset tokens 2 "actionable") ;; "actionable"
