(ns clojure.misc.spike-001)

(isa? java.util.ArrayList java.util.List) ;; true

(defn dispatch-fn [arg1 arg2]
  [arg2 arg1])

(defmulti sample-multimethod dispatch-fn)

(defmethod sample-multimethod [:second :first]
  [first second]
  [:normal-params first second])

(defmethod sample-multimethod [:first :second]
  [first second]
  [:switch-params second first])

(sample-multimethod :first :second) ;; [:normal-params :first :second]
(sample-multimethod :second :first) ;; [:switch-params :first :second]

;; From Clojure Hig Performance JVM Programming : page 91

(defn avg [& coll]
  (/ (apply + coll) (count coll)))

(defn get-race [& ages]
  (if (> (apply avg ages) 120)
    :timelord
    :human))

(defmulti travel get-race)

(defmethod travel :timelord [& ages]
  (str (count ages) "timeloards travelling by tardis"))

(defmethod travel :human [& ages]
  (str (count ages) "humans travelling by car"))

(travel 2000 1000 100 200) ;; "4timeloards travelling by tardis"
(travel 80 20 100 40) ;; "4humans travelling by car"

(map (comp - inc) [1 2 3 4 5]) ;; (-2 -3 -4 -5 -6)

(map (comp str inc) [1 2 3 4 5]) ;; ("2" "3" "4" "5" "6")

((every-pred odd?
             #(< % 12)
             #(> % 2)) 3 5 7) ;; true

((every-pred odd?
             #(< % 12)
             #(> % 2)) 3 5 7)

(filter (every-pred odd?
                    #(< % 12)
                    #(> % 2)
                    )
        (range 6) ;; (0 1 2 3 4 5)
        ) ;; (3 5)

((some-fn even?
          #(= % 10)
          #(= % 1))
 11 10 1) ;; true

((some-fn even?
          #(= % 10)
          #(= % 1))
 3 5 7) ;; false

(filter (some-fn even?
                 #(= % 10)
                 #(= % 1))
        [11 10 1 2 3]) ;; (10 1 2)

(defn factorial-recur [n]
  (loop [c n
         ret 1]
    (if (< c 2)
      ret
      (recur (dec c) (* ret c)))))

(factorial-recur 2000N) ;; no errors!
