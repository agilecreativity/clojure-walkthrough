(ns misc.spike-clojure-mixin
  (:require [clojure.string]))

;; From page 211: Clojure-style mixins
(defprotocol StringOps
  (rev [s])
  (upp [s]))

(extend-type String
  StringOps
  (rev [s]
    (clojure.string/reverse s)))

(rev "Work") ;; "kroW"

(extend-type String
  StringOps
  (upp [s]
    (clojure.string/upper-case s)))

(upp "Work") ;;
;; but now
(comment (rev "Work") ;; Evaluate this will get the IllegalArugmentException No implement..
         )

;; For this to work
(def rev-mixin {:rev clojure.string/reverse})
(def upp-mixin {:upp (fn [this] (.toUpperCase this))})
(def fully-mixed (merge upp-mixin rev-mixin))
(extend String StringOps fully-mixed)

(-> "Work" upp rev) ;; "KROW"
