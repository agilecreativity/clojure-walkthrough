(ns cfbt.ch08)

(macroexpand '(when boolean-expr
                expr1
                expr2
                expr3))
;;=> (if boolean-expr (do expr1 expr2 expr3))

;; Anatomy of a Macro
(defmacro infix
  "Use this macro when you pine for the notation of your childhood"
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))

(macroexpand '(infix (1 + 1))) ;; (+ 1 1)

(infix (1 + 1)) ;; 2

(defmacro infix-2
  [[operand1 op operand2]]
  (list op operand1 operand2))

(infix-2 (1 + 2))  ;; 3

(defmacro clj-and
  "Evalulates exprs one at a time, from left to right.."
  ([] true)
  ([x] x)
  ([x & next]
   `(let [and# ~x]
      (if and# (and ~@next) and#))))

(clj-and true true) ;; true
(clj-and) ;; true
(clj-and 2) ;; 2

;; Building lists for evaluation
(quote
 (+ 1 2))

(quote +) ;; +

(quote sweating-to-the-oldies) ;; sweating-to-the-oldies

'(+ 1 2) ;; (+ 1 2)

'dr-jekyll-and-richard-simmons ;; dr-jekyll-and-richard-simmons

;; (defmacro when
;;   "Evalulate test. If logical true, evaluates body in an implicit do."
;;   [test & body]
;;   (list 'if test (cons 'do body))
;;   )

(macroexpand '(when (the-cows-come :home)
                (call me :pappy)
                (slap me :silly))).
;;=> (if (the-cows-come :home) (do (call me :pappy) (slap me :silly)))

;; From the built-in macro `unless`

(defmacro cj-unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(macroexpand '(unless (done-been slapped? me)
                          (slap me :silly)
                          (say "I reckon that'll learn me")))
;; (unless (done-been slapped? me) (slap me :silly) (say "I reckon that'll learn me"))

;; Syntax Quoting
'+ ;; +
'clojure.core/+ ;; clojure.core/+
`+ ;; clojure.core/+
'(+ 1 2) ;; (+ 1 2)
`(+ 1 2) ;; (clojure.core/+ 1 2)
`(+ 1 ~(inc 1)) ;; (clojure.core/+ 1 2)
(list '+ 1 (inc 1)) ;; (+ 1 2)

;; Using Syntax Quoting in a Macro (p173)
