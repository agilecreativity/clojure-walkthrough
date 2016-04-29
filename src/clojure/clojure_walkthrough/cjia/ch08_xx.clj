(ns clojure-walkthrough.cjia.ch08-xx)

;; More on functional programming
(defn square [x]
  (* x x))

;; Idiomatic equivalent to empty list
(defn square-all [numbers]
  (if (empty? numbers)
    nil
    (cons (square (first numbers))
          (square-all (rest numbers)))))

(square-all [1 2 3 4 5 6]) ;; (1 4 9 16 25 36)

(defn cube [x]
  (* x x x))

(defn cube-all [numbers]
  (if (empty? numbers)
    ()
    (cons (cube (first numbers))
          (cube-all (rest numbers)))))

;; Again easy to test
(cube-all [1 2 3 4 5 6]) ;; (1 8 27 64 125 216)

(defn do-to-all [f numbers]
  (if (empty? numbers)
    ()
    (cons (f (first numbers))
          (do-to-all f (rest numbers)))))

(do-to-all cube [1 2 3 4 5 6]) ;; (1 8 27 64 125 216)

(do-to-all square [1 2 3 4 5 6]) ;; (1 4 9 16 25 36)

;; if it is too big, it will blow up!
;(do-to-all square (range 10000)) ;; StackOverflow

(defn do-to-all [f numbers]
  (lazy-seq
   (if (empty? numbers)
     ()
     (cons (f (first numbers))
           (do-to-all f (rest numbers))))))

(do-to-all square (range 10000)) ;; No StackOverflow

(take 10 (drop 10000 (do-to-all square (range 11000)))) ;; (100000000 100020001 100040004 100060009 100080016 100100025 100120036 100140049 100160064 100180081)

(take 10 (drop 10000 (do-to-all square (range 11000)))) ;; (100000000 100020001 100040004 100060009 100080016 100100025 100120036 100140049 100160064 100180081)

;; 8.1.2: reduce lists of things
(defn total-of [numbers]
  (loop [nums numbers sum 0]
    (if (empty? nums)
      sum
      (recur (rest nums) (+ sum (first nums))))))

(total-of [5 7 9 3 4 1 2 8]) ;; 39

(defn larger-of [x y]
  (if (> x y) x y))

(larger-of 20 40) ;; 40
(larger-of 30 20) ;; 30

(defn largest-of [numbers]
  (loop [l numbers candidate (first numbers)]
    (if (empty? l)
      candidate
      (recur (rest l) (larger-of candidate (first l))))))

(largest-of [5 7 9 3 4 1 2 8]) ;; 9

(largest-of []) ;; nil

(defn compute-across [func elements value]
  (if (empty? elements)
    value
    (recur func (rest elements) (func value (first elements)))))

(defn total-of [numbers]
  (compute-across + numbers 0))

(defn largest-of [numbers]
  (compute-across larger-of numbers (first numbers)))

(defn all-greater-than [threshold numbers]
  (compute-across #(if (> %2 threshold) (conj %1 %2) %1) numbers []))

(total-of [5 7 9 3 4 1 2 8]) ;; 39
(largest-of [5 7 9 3 4 1 2 8]) ;; 9

(defn all-greater-than [threshold numbers]
  (compute-across #(if (> %2 threshold) (conj %1 %2) %1) numbers []))

(all-greater-than 5 [5 7 9 3 4 1 2 8]) ;; [7 9 8]

;; Equivalent of `compute-across` using `reduce`
(defn all-greater-than [threshold numbers]
  (reduce #(if (> %2 threshold) (conj %1 %2) %1) [] numbers))

(all-greater-than 5 [5 7 9 3 4 1 2 8]) ;; [7 9 8]

;; Filtering lists of things
(defn all-lesser-than [threshold numbers]
  (compute-across #(if (< %2 threshold) (conj %1 %2) %1) numbers []))

(all-lesser-than 5 [5 7 9 3 4 1 2 8]) ;; [3 4 1 2]

(defn select-if [pred elements]
  (compute-across #(if (pred %2) (conj %1 %2) %1) elements []))

(select-if odd? [5 7 9 3 4 1 2 8]) ; [5 7 9 3 1]

(defn all-lesser-than [threshold numbers]
  (select-if #(< % threshold) numbers))

(all-lesser-than 5 [5 7 9 3 4 1 2 8]) ; [3 4 1 2]

;; the built-in filter

(filter odd? [5 7 9 3 4 1 2 8]) ; (5 7 9 3 1)

(defn all-lesser-than [threshold numbers]
  (filter #(< % threshold) numbers))

(all-lesser-than 5 [5 7 9 3 4 1 2 8]) ; (3 4 1 2)

(defn price-with-tax [tax-rate amount]
  (->> (/ tax-rate 100)
       (+ 1)
       (* amount)))

(price-with-tax 9.5M 100) ; 109.500M

(defn with-california-taxes [prices]
  (map #(price-with-tax 9.25M %) prices))

(def prices [100 200 300 400 500])

(with-california-taxes prices) ; (109.2500M 218.5000M 327.7500M 437.0000M 546.2500M)

(defn price-with-ca-tax [price]
  (price-with-tax 9.25M price))

(defn price-with-ny-tax [price]
  (price-with-tax 8.0M price))

(defn price-calculator-for-tax [state-tax]
  (fn [price]
    (price-with-tax state-tax price)))

;; Examples

(defn of-n-args [a b c d e]
  (str a b c d e))

(defn of-k-args [d e]
  (of-n-args 1 2 3 d e))

(of-k-args \a \b) ; "123ab"

(defn partially-applied [of-n-args & n-minus-k-args]
  (fn [& k-args]
    (apply of-n-args (concat n-minus-k-args k-args))))

(def of-2-args (partially-applied of-n-args \a \b \c))

(def of-3-args (partially-applied of-n-args \a \b))

(of-2-args 4 5) ; "abc45"

(of-3-args 3 4 5) ; "ab345"

;; Using built-in function `partial`

(def of-2-args (partial of-n-args \a \b \c))

(def of-3-args (partial of-n-args \a \b))

(of-2-args 4 5) ; "abc45"

(of-3-args 3 4 5) ; "ab345"

;; Defining functions

(defn select-if [pred elements]
  (compute-across #(if (pred %2) (conj %1 %2) %1) elements []))

(defn select-into-if [container pred elements]
  (compute-across #(if (pred %2) (conj %1 %2) %1) elements container))

(def numbers [4 9 5 7 6 3 8])

;; Use with list
(select-into-if () #(< % 7) numbers) ;; (3 6 5 4)

;; Use with vector
(select-into-if [] #(< % 7) numbers) ;; [4 5 6 3]

(def select-up (partial select-into-if []))

(def select-down (partial select-into-if ()))

(select-up #(< % 9) [5 3 9 6 8]) ;; [5 3 6 8]

(select-down #(< % 9) [5 3 9 6 8]) ;; (8 6 3 5)

;; 8.3: Closures

(defn adder [num1 num2]
  (let [x (+ num1 num2)]
    (fn [y]
      (+ x y))))

(def add-5 (adder 2 3))

(add-5 10) ; 15

;; Delayed computation and closures
(let [x 1
      y 0]
  (try
    (/ x y)
    (catch Exception e
      (println (.getMessage e))))) ;; REPL: "Divide by zero"; return nil

;; Let's write our own
(defn try-catch [the-try the-catch]
  (try
    (the-try)
    (catch Exception e (the-catch e))))

(let [x 1
      y 0]
  (try-catch #(/ x y)
             #(println (.getMessage %)))) ;; REPL: "Divide by zero"; return nil

;; Closures and objects

(defn new-user [login password email]
  (fn [a]
    (case a
      :login login
      :password password
      :email email
      nil)))

(def arjun (new-user "arjun" "secret" "arjun@email.com"))

(arjun :login) ;; "arjun"

(arjun :password) ;; "secret"

(arjun :email) ;; "arjun@email.com"

; on invalid key
(arjun :name) ;; nil

;; with inner access control
(defn new-user [login password email]
  (fn [a]
    (case a
      :login login
      :email email
      :password-hash (hash password)
      nil)))

(def arjun (new-user "arjun" "secret" "arjun@email.com"))

(arjun :password) ;; nil

(arjun :password-hash) ;; 1614358358

(defn new-user [login password email]
  (fn [a & args]
    (case a
      :login login
      :email email
      :authenticate (= password (first args)))))

;; Now let's try it out
(def adi (new-user "adi" "secret" "adi@email.com"))

(adi :authenticate "blah") ; false

(adi :authenticate "secret")  ;; true

;; 8.3.4 : an object system for Clojure

(defn new-class [class-name]
  (fn [command & args]
    (case command
      :name (name class-name))))

(defmacro defclass [class-name]
  `(def ~class-name (new-class '~class-name)))

(defclass Person)

(Person :name) ;; "Person"

(def some-class Person)

(some-class :name) ; "Person"

;; Creating instances

(defn new-object [klass]
  (fn [command & args]
    (case command
      :class klass)))

;; in action
(def cindy (new-object Person))

(new-object Person) ;; #function[clojure-walkthrough.cjia.ch08-xx/new-object/fn--20607]

((cindy :class) :name) ;; "Person"

(defn new-object [klass]
  (fn [command & args]
    (case command
      :class klass
      :class-name (klass :name))))

(def cindy (new-object Person))

(defn new-class [class-name]
  (fn klass [command & args]
    (case command
      :name (name class-name)
      :new (new-object klass))))

(defclass Person)

(def nancy (Person :new))

(nancy :class-name) ; "Person"

;; Objects and state

(defn new-object [klass]
  (let [state (ref {})]
    (fn [command & args]
      (case command
        :class klass
        :class-name (klass :name)
        :set! (let [[k v] args]
                (dosync (alter state assoc k v))
                nil)
        :get (let [[key] args]
               (@state key))))))

; try this out
(def nancy (Person :new))
