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

@(exec/sh ["printenv" "HOME"])
;; {:exit 0, :out "/home/bchoomnuan\n", :err nil}

;; To check if the subprocess has returned without waiting for it
(def p (exec/sh ["sleep" "5"]))

(realized? p) ;; false

;; few seconds later..
(realized? p) ;; true

;; 4.4: Accessing resource files
;; use `resources/people.edn`
(require '[clojure.java.io :as io]
         '[clojure.edn :as edn])

(->> "people.edn"
     io/resource
     slurp
     edn/read-string
     (map :language))

;; ("Lisp" "Python" "Clojure")

;; 4.5: copying files
(clojure.java.io/copy
 (clojure.java.io/file "./foo.txt")
 (clojure.java.io/file "./bar.txt"))

;; If the file is not valid
(clojure.java.io/copy
 (clojure.java.io/file "./invalid-file.txt")
 (clojure.java.io/file "./bar.txt"))

;; will get java.io.FileNotFoudException...

;; Note: it works with `InputStream`, `Reader`, byte array, or a string
(clojure.java.io/copy "some-text" (clojure.java.io/file "./baz.txt"))

;; Support of encoding as well
(clojure.java.io/copy "some-more-text"
                      (clojure.java.io/file "./baz.txt")
                      :encoding "UTF-8")

;; Safe copy of file
(defn safe-copy [src-path dst-path & opts]
  (let [source (clojure.java.io/file src-path)
        destination (clojure.java.io/file dst-path)
        options (merge {:overwrite false} (apply hash-map opts))]
    (if (and (.exists source)
             (or (:overwrite options)
                 (= false (.exists destination))))
      (try
        (= nil (clojure.java.io/copy source destination))
        (catch Exception e (str "exception: " (.getMessage e))))
      false)))

;; Let's try it
;; Note: both of the file exist locally
(safe-copy "./foo.txt" "./bar.txt") ;; false

(safe-copy "./foo.txt" "./bar.txt" :overwrite true) ;; true
