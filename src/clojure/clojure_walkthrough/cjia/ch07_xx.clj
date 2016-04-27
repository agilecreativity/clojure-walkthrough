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
