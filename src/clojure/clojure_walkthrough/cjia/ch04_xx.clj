;; Note: replace ch03-xx the right number
(ns clojure-walkthrough.cjia.ch04-xx
  (:require [clojure.data.json :as json]
            [clojure.xml :as xml-core])
  (:gen-class))

(defn ad-hoc-type-namer [thing]
  (condp = (type thing)
    java.lang.String "string"
    clojure.lang.PersistentVector "vector"))

(ad-hoc-type-namer "I am a string") ;; "string"

(ad-hoc-type-namer []) ;; "vector"

;; what about something not in the list
;(ad-hoc-type-namer {}) ; IllegalArgumentException raised!

;; Pull implementations out into a separate redef-able map
(def type-namer-implementations
  {java.lang.String (fn [thing] "string")
   clojure.lang.PersistentVector (fn [thing] "vector")})

(defn open-ad-hoc-type-namer [thing]
  (let [dispatch-value (type thing)]
    (if-let [implementation
             (get type-namer-implementations dispatch-value)]
      (implementation thing))))

(open-ad-hoc-type-namer "I am a string") ;; "string"
(open-ad-hoc-type-namer []) ;; "vector"
;; we are able
(open-ad-hoc-type-namer {}) ;; nil

;; Now let's redefine the implementation for map
(def type-namer-implementations
  (assoc type-namer-implementations
         clojure.lang.PersistentArrayMap (fn [thing] "map")))

;; Now let's give it another go
(open-ad-hoc-type-namer {}) ;; "map"

;; 4.1.3: subtype polymophism

; first attempt with ad hoc polymorphism
(defn map-type-namer [thing]
  (condp = (type thing)
    clojure.lang.PersistentArrayMap "map"
    clojure.lang.PersistentHashMap  "map"))

(map-type-namer (hash-map))  ;; "map"

;; Note the duplicate implementation
(map-type-namer (array-map)) ;; "map"

;; let try with different kind of map
;(map-type-namer (sorted-map)) ;; => throw IllegalArgumentException: No matchng clause:

;; Let's fix that
(defn subtype-map-type-namer [thing]
  (cond
    (instance? clojure.lang.APersistentMap thing) "map" ;; Note: APersistentMap is Java superclass of all maplike things in Clojure
    :else (throw (IllegalArgumentException.
                  (str "No implementation found for ") (type thing)))))

;; Now works for anything maplike!
(subtype-map-type-namer (hash-map))   ;; "map"
(subtype-map-type-namer (array-map))  ;; "map"
(subtype-map-type-namer (sorted-map)) ;; "map"

;; 4.2.1: life without multimethods
(def example-user {:login "rob"
                   :referrer "mint.com"
                   :salary 100000
                   })
(defn fee-amount [percentage user]
  (with-precision 16 :rounding HALF_EVEN
    (* 0.01M percentage (:salary user))))

(defn affiliate-fee [user]
  (case (:referrer user)
    "google.com" (fee-amount 0.01M user)
    "mint.com"   (fee-amount 0.03M user)
    (fee-amount 0.02M user)))

(affiliate-fee example-user) ;; 30.0000M

;; 4.2.2: Ad hoc polymorphism using multimethod
(defmulti affiliate-fee (fn [user] (:referrer user)))

(defmethod affiliate-fee "mint.com" [user]
  (fee-amount 0.03M user))

(defmethod affiliate-fee "google.com" [user]
  (fee-amount 0.01M user))

(defmethod affiliate-fee :default [user]
  (fee-amount 0.02M user))

(affiliate-fee example-user) ;; 30.0000M

;; 4.2.3: multiple dispatch

(def user-1 {:login    "rob"
             :referrer "mint.com"
             :salary   100000
             :rating   :rating/bronze})

(def user-2 {:login    "gordon"
             :referrer "mint.com"
             :salary   80000
             :rating   :rating/silver})

(def user-3 {:login    "kyle"
             :referrer "google.com"
             :salary   90000
             :rating   :rating/silver})

(def user-4 {:login    "celeste"
             :referrer "yahoo.com"
             :salary   70000
             :rating   :rating/platinum})

;; Let's use the data
(defn fee-category [user]
  [(:referrer user) (:rating user)])

(map fee-category [user-1 user-2 user-3 user-4]) ;; (["mint.com" :rating/bronze] ["mint.com" :rating/silver] ["google.com" :rating/silver] ["yahoo.com" :rating/platinum])

;;(defmulti  profit-based-affiliate-fee fee-category)
;;(defmethod profit-based-affiliate-fee ["mint.com" :rating/bronze]
;[user] (fee-amount 0.03M))
