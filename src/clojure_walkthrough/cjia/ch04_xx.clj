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
(ad-hoc-type-namer {}) ; IllegalArgumentException raised!

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
