(ns cjcb.codes)

;; 4.1:
(println "This text will be printed to STDOUT")
(do
  (print "a")
  (print "b"))

(binding [*out* *err*]
  (println "Blew up!"))

;; Create a writer to file foo.txt and print to it.
(def foo-file (clojure.java.io/writer "foo.txt"))

(binding [*out* foo-file]
  (println "Foo, bar, baz!"))

;; Nothing is printed to *out*
;; and now we close the file
(.close foo-fil)
