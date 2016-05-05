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

(defmacro code-critic
  "Phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(code-critic (1 + 1) (+ 1 1))
l;; => "Great squid of Madrid, this is bad code: (1 + 1)
;;    "Sweet gorilla of Manila, this is good code: (+ 1 1)

;; Use of syntax quoting
(defmacro code-critic
  [bad good]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))

(code-critic (1 + 1) (+ 1 1))

;; Refactoring a Macro and Unquote Splicing
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic
  [bad good]
  `(do ~(criticize-code "Curses bacteria of Liberta, this is bad code:" bad)
       ~(criticize-code "Sweet secred boa of Western and Easten Samoa, this is good code: " good)))

(code-critic (1 * 2) (* 1 2))

;; remove some duplication
;; (defmacro code-critic
;;   [bad good]
;;   `(do ~(map #(apply criticize-code %)
;;              [["This is bad code: " bad]
;;               ["This is great code: " good]])))
`(+ ~(list 1 2 3))
;; (clojure.core/+ (1 2 3))

`(+ ~@(list 1 2 3))
;; (clojure.core/+ 1 2 3)

(defmacro code-critic
  [good bad]
  `(do ~@(map #(apply criticize-code %)
              [["Sweet lion of Zion, this is bad code:" bad]
               ["Great cow of Moscow, this is good code:" good]])))

(code-critic (3 * 4) (* 4 3))
;; Sweet lion of Zion, this is bad code: (* 4 3)
;; Great cow of Moscow, this is good code: (3 * 4)

;; Things to Watch Out For
(def message "Good job!")

 (defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh, big deal!"])
          stuff-to-do))

(with-mischief
  (str "Here's how I feel about that thing you did " message))
;; => "Here's how I feel about that thing you did Oh, big deal!"

(defmacro with-mischief
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
     ~@stuff-to-do))

;; (with-mischief
;;   (str "Here's how I feel about that thing you did: " message))
;; RuntimeException: => Can't let qualified name: cfbt.ch08/message

;;(gensym)
(gensym 'message) ;; message25849
(gensym 'message) ;; message25852

;; Here is how to fix it
(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))

(without-mischief
 (println "Here's how I feel about that thing you did: " message))

;; Double Evaluation (p178): TBC
; code that does not work in the expected way
(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "was not successful:" ~to-try)))

(report (do (Thread/sleep 1000) (+ 1 1)))

; Proper way to avoid the double problem with auto-gensym's symbol
(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println (quote ~to-try) "was successful:" result#)
       (println (quote ~to-try) "was not successfull:" result#))))

(report (do (Thread/sleep 1000) (+ 1 1)))

;; Macros All the Way Down
(report (= 1 1))
;; => (= 1 1) was successful: true

(report (= 1 2))
;; => (= 1 2) was not successfull: false

(doseq [code ['(= 1 1) '(= 1 2)]]
  (report code))

(defmacro doseq-macro
  [macroname & args]
  `(do
     ~@(map (fn [arg] (list macroname arg)) args)))

(doseq-macro report (= 1 1) (= 1 2))
;; (= 1 1) was successful: true
;; (= 1 2) was not successfull: false

;; Validation Function (p180)
