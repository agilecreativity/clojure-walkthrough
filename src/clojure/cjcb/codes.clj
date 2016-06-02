(ns cjcb.codes
  (:require [clj-commons-exec :as exec]))

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

;; 4.2: read single keystrok from the console
(ns keystroke.core
  (:import [jline.console ConsoleReader]))

(defn show-keystroke []
  (print "Enter a keystroke: ")
  (flush)
  (let [cr (ConsoleReader.)
        keyint (.readCharacter cr)]
    (println (format "Got %d ('%c')!" keyint (char keyint)))))

;; 4.3: Executing system commands
;; Use 'clj-commons-exec'
(require '[clj-commons-exec :as exec])
(def p (exec/sh ["date"]))

(deref p) ;; {:exit 0, :out "Wed Jun  1 22:27:57 EDT 2016\n", :err nil}

;; for one that require arguments
@(exec/sh ["ls" "-l" "/etc/passwd"])
;; {:exit 0, :out "-rw-r--r-- 1 root root 1737 May 16 15:48 /etc/passwd\n", :err nil}

@(exec/sh ["ls" "-l" "invalid-file-name"])
 ;; {:exit 2, :out nil, :err "ls: cannot access 'invalid-file-name': No such file or directory\n", :exception #error {
 ;; :cause "Process exited with an error: 2 (Exit value: 2)"  ...
