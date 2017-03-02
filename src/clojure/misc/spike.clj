(ns others.spike)

;; See: http://stackoverflow.com/questions/14763301/calling-clojure-functions-using-var-quote-syntax

(defn a [] 1)

(let [a (fn [] "foo")]
  [(a) (#'a)]) ;; ["foo" 1]

(defn my-call [f] (fn [] (+ 1 (f))))

(def one (my-call a)) ;; #'web-login.spike/one

(def two (my-call #'a)) ;; #'web-login.spike/two

(defn a [] 2)

(one) ;; 2

(two) ;; 3

;; To use it to get around to the private method
(defn- a [] 1)

(ns user2)

(comment
  (user2/a)) ;; will give you errors

;; But this will allow you to call without any problem
(comment (#'others.spike/a))

;; See [this link](http://clojure.org/reference/special_forms)
(defn
  ^{:doc "mymax [xs+] gets the maximum value in xs using > "
    :test (fn []
            (assert (= 42 (mymax 2 42 5 4))))
    :user/comment "this is the best fn ever!"}
  mymax
  ([x] x)
  ([x y] (if (> x y) x y))
  ([x y & more]
   (reduce mymax (mymax x y) more)))

((:test (meta #'mymax))) ;; {:doc "mymax [xs+] gets the maximum value in xs using > ", :test #function[user2/fn--23578], :user/comment "this is the best fn ever!", :arglists ([x] [x y] [x y & more]), :line 34, :column 1, :file "/home/bchoomnuan/projects/clojure-prj/web-login/src/web_login/spike.clj", :name mymax, :ns #namespace[user2]}
               ;; {:doc "mymax [xs+] gets the maximum value in xs using > ", :test #function[user2/fn--23562], :user/comment "this is the best fn ever!", :arglists ([x] [x y] [x y & more]), :line 33, :column 1, :file "/home/bchoomnuan/projects/clojure-prj/web-login/src/web_login/spike.clj", :name mymax, :ns #namespace[user2]}
(comment {:doc "mymax [xs+] gets the maximum value in xs using > ",
          :test #function[user2/fn--23562],
          :user/comment "this is the best fn ever!",
          :arglists ([x] [x y] [x y & more]),
          :line 33,
          :column 1,
          :file "/home/bchoomnuan/projects/clojure-prj/web-login/src/web_login/spike.clj",
          :name mymax,
          :ns #namespace[user2]})

;; See also:
;; https://clojure.github.io/clojure/clojure.test-api.html
;; https://en.wikibooks.org/wiki/Learning_Clojure/Special_Forms

;; From the book Joy of Clojure
(defmacro do-until [& clauses]
  (when clauses
    (list 'clojure.core/when (first clauses)
          (if (next clauses)
            (second clauses)
            (throw (IllegalArgumentException.
                    "do-until require an even number of forms")))
          (cons 'do-until (nnext clauses)))))

;; What is nnext is same as (next (next x))
(macroexpand-1 '(do-until true (prn 1) false (prn 2)))
;; (clojure.core/when true (prn 1) (do-until false (prn 2)))

(defmacro my-unless [condition & body]
  `(if (not ~condition)
     (do ~@body)))

(my-unless false (do
                  (println "nope")
                  (+ 10 20 30))) ;; 60

(my-unless true (println "nope")) ;; nil

;; See: (doc add-watch)
(defmacro def-watched [name & value]
  `(do
     (def ~name ~@value)
     (add-watch (var ~name)
                :re-bind
                (fn [~'key ~'r old# new#]
                  (println old# " -> " new#)))))

;; When we expand them
(def-watched x 2) ;;
(macroexpand '(def-watched x 2))
;; (do (def x 2) (clojure.core/add-watch (var x) :re-bind (clojure.core/fn [key r old__21449__auto__ new__21450__auto__] (clojure.core/println old__21449__auto__ " -> " new__21450__auto__))))

(def-watched x (* 12 12))
x ;; 144

(defmacro awhen [expr & body]
  `(let [~'it ~expr]
     (if ~'it
       (do ~@body))))

(awhen [1 2 3] (it 2)) ;; 3

(macroexpand-1 '(awhen [1 2 3] (it 2))) ;; 3
;; (clojure.core/let [it [1 2 3]] (if it (do (it 2))))

(awhen nil (println "Will never get here")) ;; nil

(awhen 1 (awhen 2 [it])) ;; [2] ;; fail the nest!

(defmacro def-find-by [expr & body]
  `(let [~'find-by '#(symbol (str "browser/find-element-by-" ~expr))]
     ;;
     (list ~'find-by
           ~@body)))

(macroexpand `(def-find-by "id" "login_field" "john"))

(def-stuff "id" "login_field" "john")

(eval (list (symbol "+") 1 2))

(eval (list  (symbol "+") 2 3 4)) ;; 9

;; Contextual eval
(defn contextual-eval [ctx expr]
  (eval
   `(let [~@ (mapcat (fn [[k v]]
                       [k `'~v]) ctx)]
      ~expr)))

(contextual-eval '{a 1, b 2} '(+ a b))
;; 3

;; Sample DSL usage from 8.7 of Joy of Clojure
(comment (contract doubler [x]
           (:require (pos? x))
           (:ensure
            (= (* 2 x) %))))

(declare collect-bodies
         build-contract)

(defmacro contract [name & forms]
  (list* `fn name (collect-bodies forms)))

(fn doubler
  ([f x]
   {:post [(= (* 2 x) %)]
    :pre [(pos? x)]}
   (f x)))

(defn build-contract [c]
  (let [args (first c)]
    (list
     (into '[f] args)
     (apply merge
            (for [con (rest c)]
              (cond (= (first con) 'require)
                    (assoc {} :pre (vec (rest con)))
                    (= (first con) 'ensure)
                    (assoc {} :post (vec (rest con)))
                    :else (throw (Exception.
                                  (str "Unknown tag "
                                       (first con)))))))
     (list* 'f args))))

(defn collect-bodies [forms]
  (for [form (partition 3 forms)]
    (build-contract form)))

(def doubler-contract
  ;; define contract
  (contract doubler
            [x]
            (require
             (pos? x))
            (ensure
             (= (* 2 x) %))))

(def times2 (partial doubler-contract #(* 2 %)))

;; Test correct use
(times2 9) ;; 18

;; Test incorrect use
(def times3 (partial doubler-contract #(* 3 %)))
(times3 9) ;; => Assert failed: (= (* 2  x) %)
