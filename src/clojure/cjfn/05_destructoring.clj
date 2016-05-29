(ns cjfn.05-destructoring)

;; 05-03: Sequential Destructuring
(def list-01 [7 8 9 10 11])

;; Bind a, b and c to the first 3 values in stuff
(let [[a b c] list-01]
  (list (+ a b) (+ b c))) ;; (15 17)

;; If more keys are provided then nil will be used for unbounded value
(let [[a b c d e f] list-01]
     (list d e f)) ;; (10 11 nil)

;; 05-04: Sequential Destructuring
(def list-02 [7 8 9 10 11])
(let [[a & others] list-02]
  (str "first: " a ", others: " others)) 
;; "first: 7, others: (8 9 10 11)"

;; 05-05: ignoring destructured values with _
(def list-03 [7 8 9 10 11])
(let [[_ & others] list-03]
  (str "others: " others)) ;; "others: (8 9 10 11)"

;; 05-06: examples
(def names ["Bob" "James" "Joe"])
(let [[first-name] names]
  first-name) ;; "Bob"

;; 2nd value in the list
(let [[_ second-name] names] second-name) ;; "James"

;; Or use all the rest of the parameter
(let [[& rest-names] names] rest-names) ;; ("Bob" "James" "Joe")

;; drop the first item
(let [[_ & rest-names] names] rest-names) ;; ("James" "Joe")

;; Use it in the function
(defn rest-of-names [[_ & rest-names]]
  rest-names)

(rest-of-names ["Bob" "James" "John"]) ;; ("James" "John")

;; Since the _ is valid name in clojure 
(let [[_ & rst-names] names] _) ;; "Bob"

;; 05-07: associative destructuring

(def m {:a 7 :b 4})

(let [{:keys [a b]} m]
  [a b]) ;; [7 4]

;; if the key is invalid the result will be nil
(let [{:keys [a b c]} m]
  [a b c]) ;; [7 4 nil]

;; Using :default to provide default values for bound keys
(let [{:keys [a b c]
       :or {c 3}} m]
  [a b c]) ;; [7 4 3]

;; What if input were to contain the value with key c
(let [{:keys [a b c]
       :or {c 3}} {:a 1 :b 2 :c 3}]
  [a b c]) ;; [1 2 3]

;; Named arguments
(defn game [planet & {:keys [human-players computer-players]}]
  (str "Total players: " (+ human-players computer-players)))

(game "Mars" :human-players 1 :computer-players 2)
;; "Total players: 3"

;; 05-11: Associative destructoring demo

(defn draw-point-2d [& {x :x y :y}]
  [x y])

(draw-point-2d :x 10 :y 20) ;; [10 20]
(draw-point-2d :y 20 :x 10) ;; [10 20]

(defn draw-point-3d [& {:keys [x y z]}]
  [x y z])

(draw-point-3d :x 10 :y 20 :z 30)  ;; [10 20 30]
(draw-point-3d :y 20 :x 10 :z 30)  ;; [10 20 30]

;; If the z is missing
(draw-point-3d :x 10 :y 20) ;; [10 20 nil]

;; Use of default value for z
(defn draw-point-3d [& {:keys [x y z]
                        :or {x 0
                             y 0
                             z 0}}]
  [x y z])

;; Left out one
(draw-point-3d :x 10 :y 20) ;; [10 20 0]

;; Left out all of them
(draw-point-3d) ;; [0 0 0]
