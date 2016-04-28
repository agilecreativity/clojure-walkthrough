(ns clojure-walkthrough.cjia.ch07-xx)
; 7.1: Macro basic

; 7.1.1: Textual substitution

(def a-ref (ref 0))

(dosync
 (ref-set a-ref 1)) ; 1

;; You could implement this using the macros like
; (syn-set a-ref 1)

(defmacro sync-set [r v]
  (list 'dosync
        (list 'ref-set r v)))

(sync-set a-ref 1) ;; 1

;; 7.1.2: the unless example

; we want to write the reverse of (if test then else)
(defn exhibits-oddity? [x]
  (if (odd? x)
    (str x " is very odd!")))

(exhibits-oddity? 3) ; "3 is very odd!"
(exhibits-oddity? 2) ; nil

; we want to be able to implement unless like in ruby
;; (defn exhibits-oddity? [x]
;;   (unless (odd? x)
;;           (str x "is very odd!")))
; this will not work as we don't have `unless` in Clojure
; but we can implement this using macros

(defn unless [test then]
  (if (not test)
    then))

(exhibits-oddity? 10) ; nil?
(unless (exhibits-oddity? 10) (println "it is really odd!"))

(defn exhibits-oddity? [x]
  (unless (even? x)
          (println "Rather odd")))

(exhibits-oddity? 11) ;; REPL: "Rather odd!" and return nil

(exhibits-oddity? 10) ;; nil

;; The unless macro

(defmacro unless [test then]
  (list 'if (list 'not test)
        then))

(defn exhibits-oddity? [x]
  (unless (even? x)
          (println "Very odd, indeed!")))

(exhibits-oddity? 10) ; nil
(exhibits-oddity? 11) ; REPL: "Very odd, indeed!" then return

(macroexpand '(unless (even? x) (println "Very odd, indeed!"))) ;; (if (not (even? x)) (println "Very odd, indeed!"))

;; Other useful functions are
; `macroexpand-1`, `macroexpand-all` from `clojure.walk`

(macroexpand '(unless (even? x) (println "Very odd, indeed!"))) ;; (unless (even? x) (println "Very odd, indeed!"))

(defn exhibits-oddity? [x]
  (unless (even? x)
          (do
            (println "Odd!")
            (println "Very odd!"))))

(exhibits-oddity? 4) ; nil
(exhibits-oddity? 3) ; REPL: "Odd!" and "Very odd!"

(defmacro unless [test & exprs]
  `(if (not ~test)
     (do ~exprs)))

(defmacro unless [test & exprs]
  `(if (not ~test)
     (do ~@exprs)))
;
(:import 'java.lang.System)
(System/currentTimeMillis)

(defmacro def-logged-fn [fn-name args & body]
  `(defn ~fn-name ~args
     (let [now# (System/currentTimeMillis)]
       (println "[" now# "]Call to" (str (var ~fn-name)))
     ~@body)))

(macroexpand-1 '(def-logged-fn printname [name]
                  (println "Hi" name))) ;; (clojure.core/defn printname [name] (clojure.core/let [clojure-walkthrough.cjia.ch07-xx/now (java.lang.System/currentTimeMillis)] (clojure.core/println "[" clojure-walkthrough.cjia.ch07-xx/now "]Call to" (clojure.core/str (var printname))) (println "Hi" name)))

(def-logged-fn printname [name]
  (println "Hi" name))

(printname "Arun")

;; 7.1.2: comment
; (defmacro comment [& body])

(defmacro declare [& names]
  `(do
     ~@(map #(list 'def %) names)))

(macroexpand '(declare add multiply subtract divide)) ; (do (def add) (def multiply) (def subtract) (def divide))

;; defonce
(defmacro defonce [name expr]
  `(let [v# (def ~name)]
     (when-not (.hasRoot v#)
       (def ~name ~expr))))

;; and
(defmacro and
  ([] true)
  ([x] x)
  ([x & next]
   `(let [and# ~x]
      (if and# (and ~@next) and#))))

(and 2)
(and [true 3]) ; [true 3]
(and [(+ 4 5) nil 9])
(and) ; true

(macroexpand '(and (even? x) (> x 50) (< x 500))) ; (let* [and__20455__auto__ (even? x)] (if and__20455__auto__ (clojure-walkthrough.cjia.ch07-xx/and (> x 50) (< x 500)) and__20455__auto__))

;; time:

(time (* 1331 13531)) ; 18009761

;; Let's implement our own
(defmacro time [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (prn
      (str "Elapsed time:"
           (/ (double (- (. System (nanoTime)) start#)) 10000000.0)
           " msecs"))
     ret#)) ; #'clojure-walkthrough.cjia.ch07-xx/time

(time (* 1345 5979)) ; 8041755

;; 7.3: writing our own macro

(defmacro randomly [& exprs]
  (let [len (count exprs)
        index (rand-int len)
        conditions (map #(list '= index %) (range len))]
    `(cond ~@(interleave conditions exprs))))

(randomly (println "Amit") (println "Deepthi" (println "Adi")))
(macroexpand-1 '(randomly (println "Amit") (println "Deepthi" (println "Adi")))) ; (clojure.core/cond (= 0 0) (println "Amit") (= 0 1) (println "Deepthi" (println "Adi")))

(defmacro randomly-2 [& exprs]
  (nth exprs (rand-int (count exprs))))

;; it returns the same random value!
(randomly-2 (println "Amit") (println "Deepthi" (println "Adi")))

;; The fix
(defmacro randomly-2 [& exprs]
  (let [c (count exprs)]
    `(case (rand-int ~c) ~@(interleave (range c) exprs))))

(randomly-2 (println "Amit") (println "Deepthi" (println "Adi"))) ;; return proper result!

(defn check-credentials [username password]
  ; Just return true for now
  true)

(defn login-user [request]
  (let [username (:username request)
        password (:password request)]
    (if (check-credentials username password)
      (str "Welcome back, " username ", " password "is correct!")
      (str "Login failed!"))))

(def request {:username "amit"
              :password "1234"})

nv(login-user request) ; "Welcome back, amit, 1234is correct!"

;; Let's create the macro
(defmacro defwebmethod [name args & exprs]
  `(defn ~name [{:keys ~args}]
     ~@exprs))

(defwebmethod login-user [username password]
  (if (check-credentials username password)
    (str "Welcome, " username "," password " is still correct!")
    (str "Login failed!")))

(login-user request) ; "Welcome, amit,1234 is still correct!"

;; 7.3.4: defnn
(defmacro defnn [fname [& names] & body]
  (let [ks {:keys (vec names)}]
    `(defn ~fname [& {:as arg-map#}]
       (let [~ks arg-map#]
         ~@body))))

(defnn print-details [name salary start-date]
  (println "Name     : " name)
  (println "Salary   : " salary)
  (println "Start on : " start-date))

(print-details :start-date "10/22/2009"
               :name "Rob"
               :salary 1000000)

;; assert-true
(defmacro assert-true [test-expr]
  (let [[operator lhs rhs] test-expr]
   `(let [rhsv# ~rhs ret# ~test-expr]
      (if-not ret#
        (throw (RuntimeException.
                (str '~lhs " is not " '~operator " " rhsv#)))
        true
        ))))

; We like to use this one this way!
(assert-true (= (* 2 4) (/ 16 2))) ; true

(assert-true (< (* 2 4) (/ 18 2))) ; true

(assert-true (< (* 4 5) (* 2 5))) ;; java.lang.RuntimeException
                                  ;; (* 4 5) is not < 10
(macroexpand-1 '(assert-true (>= (* 2 4) (/ 18 2)))) ;; (clojure.core/let [rhsv__20666__auto__ (/ 18 2) ret__20667__auto__ (>= (* 2 4) (/ 18 2))] (clojure.core/if-not ret__20667__auto__ (throw (java.lang.RuntimeException. (clojure.core/str (quote (* 2 4)) " is not " (quote >=) " " rhsv__20666__auto__))) true))

;; What if invalid expression are passed in
(defmacro assert-true [test-expr]
  (if-not (= 3 (count test-expr))
    (throw (RuntimeException.
            "Argument must be of the form (operator test-expr expected-expr)")))
  (if-not (some #{(first test-expr)} '(< > <= >= = not=))
    (throw (RuntimeException.
            "Operator must be one of < > <= >= = not=")))
  (let [[operator lhs rhs] test-expr]
    `(let [rhsv# ~rhs ret# ~test-expr]
       (if-not ret#
         (throw (RuntimeException.
                 (str '~lhs " is not " '~operator " " rhsv#)))
         true))))

;; (assert-true (<> (* 2 4) (/ 18 2) (+ 2 5))) ;; => Argument must be of the form (operator test-expr expected-expr)

;; (assert-true (<> (* 2 4) (/ 16 2))) ;; => Operator must be one of < > <= >= = not=

