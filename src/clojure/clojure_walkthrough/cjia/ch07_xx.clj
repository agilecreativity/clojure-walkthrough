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
