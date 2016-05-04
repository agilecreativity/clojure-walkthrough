(ns clojure.cfbt.ch07)

(defmacro backwards
  [form]
  (reverse form))

(backwards (" backwards" " am" "I" str)) ;; "I am backwards"

(def addition-list (list + 1 2)) ;; #'clojure.cfbt.ch07/addition-list

(eval addition-list) ;; 3

(eval (concat addition-list [10])) ;; 13

(eval (list 'def `lucky-number (concat addition-list [10])))
;; #'clojure.cfbt.ch07/lucky-number
lucky-number ;; 13

(str "To understand what recursion is," " you must first understand recursion") ;;
;; "To understand what recursion is, you must first understand recursion"

(read-string "(+ 1 2)") ;; (+ 1 2)

(list? (read-string "(+ 1 2)")) ;; true

(conj (read-string "(+ 1 2)") :zagglewag) ;; (:zagglewag + 1 2)

(eval (read-string "(+ 1 2)")) ;; 3

(#(+ 1 %) 3) ;; 4

(read-string "#(+ 1 %)") ;; (fn* [p1__25713#] (+ 1 p1__25713#))

;; Reader Macros
(read-string "'(a b c)") ;; (quote (a b c))

(read-string "@var") ;; (clojure.core/deref var)

(read-string "; ignore!\n(+1 2)") ;; (1 2)

;; The Evaluator

(if true :a :b) ;; :a

(let [x 5]
  (+ x 3)) ;; 8

(def x 15)

(let [x 5]
  (+ x 3)) ;; 8

(let [x 5]
  (let [x 6]
    (+ x 3))) ;; 9

(defn exclaim
  [exclamation]
  (str exclamation "!"))

(exclaim "Hadoken") ;; "Hadoken!"

(map inc [1 2 3]) ;; (2 3 4)

(read-string "+") ;; +

(type (read-string "+")) ;; clojure.lang.Symbol

(list (read-string "+") 1 2) ;; (+ 1 2)

(eval (list (read-string "+") 1 2)) ;; 3

;; Lists

(eval (read-string "()")) ;; ()

;; Special Forms
(if true 1 2) ;; 1

'(a b c) ;; (a b c)

(quote (a b c)) ;; (a b c)

;; Macros

(read-string "(1 + 1)") ;; (1 + 1)

;(eval (read-string "(1 + 1)"))
;; ClassCastExceptiooon
(let [infix (read-string "(1 + 1)")]
  (list (second infix) (first infix) (last infix))) ;; (+ 1 1)

(eval
 (let [infix (read-string "(1 + 1)")]
   (list (second infix) (first infix) (last infix)))) ;; 2

;; Use of macro to clean this up
(defmacro ignore-last-operand
  [function-call]
  (butlast function-call))

(ignore-last-operand (+ 1 2 10)) ;; 3

;; this will not print anything
(ignore-last-operand (+ 1 2 (print "Look at me!!!")))
;;=> 3

(macroexpand '(ignore-last-operand (+ 1 2 10)))
;; (+ 1 2)

(macroexpand '(ignore-last-operand (+ 1 2 (println "look at me!!!"))))
;; (+ 1 2)

(defmacro infix
  [infixed]
  (list (second infixed)
        (first infixed)
        (last infixed)))

(infix (1 + 2)) ;; 3

;; Syntactic Abstraction and the -> Macro

(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (clojure.java.io/resource path))))

(defn read-resource
  [path]
  (-> path
      clojure.java.io/resource
      slurp
      read-string))
