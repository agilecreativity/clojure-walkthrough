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

(with-open [reader (clojure.java.io/reader "./foo.txt")
            writer (clojure.java.io/writer "./bar.txt")]
  (clojure.java.io/copy reader writer))

;; 4.6: deleting files or directories
(let [silently true]
 (clojure.java.io/delete-file "./baz.txt" silently)) ;; true

(defn safe-delete [file-path]
  (if (.exists (clojure.java.io/file file-path))
    (try
      (clojure.java.io/delete-file file-path)
      (catch Exception e (str "Exception: " (.getMessage e))))
    false))

(safe-delete "./foo.txt") ;; true
(safe-delete "./invalid-file.txt") ;; false

;; Delete directory
(defn delete-directory [dir-path]
  (let [dir-contents (file-seq (clojure.java.io/file dir-path))
        files-to-delete (filter #(.isFile %) dir-contents)]
    (doseq [file files-to-delete]
      (safe-delete (.getPath file)))
    (safe-delete dir-path)))

;; If we have just one level of directory without sub-directory
(delete-directory "./tmp") ;; true

;; 4.7: listing files in a directory using `file-seq` function
(def list-file (file-seq (clojure.java.io/file "./src")))

list-file ;; (#object[java.io.File 0x29923f72 "./src"] #object[java.io.File 0x6f993ddc "./src/java"] #object[java.io.File 0x316a75a9 "./src/java/com"] #object[java.io.File 0x4c981747 "./src/java/com/examples"] #object[java.io.File 0x7449d809 "./src/java/com/examples/Client.java"] #object[java.io.File 0x52986c81 "./src/java/com/gentest"] #object[java.io.File 0x303d9c46 "./src/java/com/gentest/AbstractJavaClass.java"] #object[java.io.File 0x3c939729 "./src/clojure"] #object[java.io.File 0x6a09922 "./src/clojure/com"] #object[java.io.File 0x17cd0113 "./src/clojure/com/curry"] #object[java.io.File 0x4bce6000 "./src/clojure/com/curry/utils"] #object[java.io.File 0x6bc49c8f "./src/clojure/com/curry/utils/calculator.clj"] #object[java.io.File 0x69af715c "./src/clojure/com/curry/utils/calc"] #object[java.io.File 0x169520e3 "./src/clojure/com/curry/utils/calc/dcf.clj"] #object[java.io.File 0x2e639c51 "./src/clojure/com/curry/utils/calc/fcf.clj"] #object[java.io.File 0x610c4df6 "./src/clojure/com/gentest"] #object[java.io.File 0x733be51a "./src/clojure/com/gentest/genclojure.clj"] #object[java.io.File 0x4c9651a6 "./src/clojure/mcj"] #object[java.io.File 0x7ea5dd71 "./src/clojure/mcj/ch_xx.clj"] #object[java.io.File 0x7d454cae "./src/clojure/mcj/c02_agents.clj"] #object[java.io.File 0x517a7529 "./src/clojure/cjapplied"] #object[java.io.File 0x2f1f15c1 "./src/clojure/cjapplied/ch08.clj"] #object[java.io.File 0x7f011b06 "./src/clojure/cjfn"] #object[java.io.File 0x28f61874 "./src/clojure/cjfn/05_destructoring.clj"] #object[java.io.File 0x2a27f37e "./src/clojure/cjfn/07_control_flow.clj"] #object[java.io.File 0x7dc15ae6 "./src/clojure/cjfn/README.md"] #object[java.io.File 0xaefa773 "./src/clojure/cjfn/09_sequences.clj"] #object[java.io.File 0x12423be2 "./src/clojure/clojure"] #object[java.io.File 0x7eb86d7 "./src/clojure/clojure/script"] #object[java.io.File 0x708afaa6 "./src/clojure/clojure/script/examples.clj"] #object[java.io.File 0x7a91ffb "./src/clojure/cjcb"] #object[java.io.File 0x437b9c7 "./src/clojure/cjcb/.#codes.clj"] #object[java.io.File 0x5fb14aa8 "./src/clojure/cjcb/codes.clj"] #object[java.io.File 0x78c9c432 "./src/clojure/cjcb/#codes.clj#"] #object[java.io.File 0x37504a17 "./src/clojure/cjcb/.# *Minibuf-2*"] #object[java.io.File 0xe9034bb "./src/clojure/cfbt"] #object[java.io.File 0x5b21dbcb "./src/clojure/cfbt/ch09.clj"] #object[java.io.File 0x6d7f6cc9 "./src/clojure/cfbt/ch05.clj"] #object[java.io.File 0x58ca8383 "./src/clojure/cfbt/ch08.clj"] #object[java.io.File 0x577051cd "./src/clojure/cfbt/ch04.clj"] #object[java.io.File 0x2da376b "./src/clojure/cfbt/ch06.clj"] #object[java.io.File 0x14d51add "./src/clojure/cfbt/ch07.clj"] #object[java.io.File 0x180f455f "./src/clojure/cfbt/ch10.clj"] #object[java.io.File 0x4da491aa "./src/clojure/clojure_walkthrough"] #object[java.io.File 0x1e96861a "./src/clojure/clojure_walkthrough/cjia"] #object[java.io.File 0x22a964e7 "./src/clojure/clojure_walkthrough/cjia/ch05_xx.clj"] #object[java.io.File 0x406ee850 "./src/clojure/clojure_walkthrough/cjia/ch03_06.clj"] #object[java.io.File 0x29ae0836 "./src/clojure/clojure_walkthrough/cjia/ch04_xx.clj"] #object[java.io.File 0x322ed4f "./src/clojure/clojure_walkthrough/cjia/ch06_xx.clj"] #object[java.io.File 0x6a452f90 "./src/clojure/clojure_walkthrough/cjia/ch09_xx.clj"] #object[java.io.File 0x5d5e66e0 "./src/clojure/clojure_walkthrough/cjia/ch08_object.clj"] #object[java.io.File 0x4683ac5 "./src/clojure/clojure_walkthrough/cjia/ch03_05.clj"] #object[java.io.File 0x10b4f792 "./src/clojure/clojure_walkthrough/cjia/ch08_xx.clj"] #object[java.io.File 0x53ce63bb "./src/clojure/clojure_walkthrough/cjia/ch03_04.clj"] #object[java.io.File 0x7d096644 "./src/clojure/clojure_walkthrough/cjia/ch03_07.clj"] #object[java.io.File 0x3872d532 "./src/clojure/clojure_walkthrough/cjia/ch07_xx.clj"] #object[java.io.File 0xfcdbdc9 "./src/clojure/clojure_walkthrough/cjia/ch03_03.clj"] #object[java.io.File 0x2f3ec430 "./src/clojure/clojure_walkthrough/cjia/readme.md"] #object[java.io.File 0x19b3acd5 "./src/clojure/clojure_walkthrough/cjia/ch10_xx.clj"] #object[java.io.File 0x37c63194 "./src/clojure/clojure_walkthrough/core.clj"] #object[java.io.File 0x5507f150 "./src/resources"])

;; Let's list only files

(defn only-files
  "Filter a sequence of files/directories by .isFile property of java.io.File"
  [file-s]
  (filter #(.isFile %) file-s))

(only-files list-file) ;; (#object[java.io.File 0x7449d809 "./src/java/com/examples/Client.java"] #object[java.io.File 0x303d9c46 "./src/java/com/gentest/AbstractJavaClass.java"] #object[java.io.File 0x6bc49c8f "./src/clojure/com/curry/utils/calculator.clj"] #object[java.io.File 0x169520e3 "./src/clojure/com/curry/utils/calc/dcf.clj"] #object[java.io.File 0x2e639c51 "./src/clojure/com/curry/utils/calc/fcf.clj"] #object[java.io.File 0x733be51a "./src/clojure/com/gentest/genclojure.clj"] #object[java.io.File 0x7ea5dd71 "./src/clojure/mcj/ch_xx.clj"] #object[java.io.File 0x7d454cae "./src/clojure/mcj/c02_agents.clj"] #object[java.io.File 0x2f1f15c1 "./src/clojure/cjapplied/ch08.clj"] #object[java.io.File 0x28f61874 "./src/clojure/cjfn/05_destructoring.clj"] #object[java.io.File 0x2a27f37e "./src/clojure/cjfn/07_control_flow.clj"] #object[java.io.File 0x7dc15ae6 "./src/clojure/cjfn/README.md"] #object[java.io.File 0xaefa773 "./src/clojure/cjfn/09_sequences.clj"] #object[java.io.File 0x708afaa6 "./src/clojure/clojure/script/examples.clj"] #object[java.io.File 0x5fb14aa8 "./src/clojure/cjcb/codes.clj"] #object[java.io.File 0x78c9c432 "./src/clojure/cjcb/#codes.clj#"] #object[java.io.File 0x5b21dbcb "./src/clojure/cfbt/ch09.clj"] #object[java.io.File 0x6d7f6cc9 "./src/clojure/cfbt/ch05.clj"] #object[java.io.File 0x58ca8383 "./src/clojure/cfbt/ch08.clj"] #object[java.io.File 0x577051cd "./src/clojure/cfbt/ch04.clj"] #object[java.io.File 0x2da376b "./src/clojure/cfbt/ch06.clj"] #object[java.io.File 0x14d51add "./src/clojure/cfbt/ch07.clj"] #object[java.io.File 0x180f455f "./src/clojure/cfbt/ch10.clj"] #object[java.io.File 0x22a964e7 "./src/clojure/clojure_walkthrough/cjia/ch05_xx.clj"] #object[java.io.File 0x406ee850 "./src/clojure/clojure_walkthrough/cjia/ch03_06.clj"] #object[java.io.File 0x29ae0836 "./src/clojure/clojure_walkthrough/cjia/ch04_xx.clj"] #object[java.io.File 0x322ed4f "./src/clojure/clojure_walkthrough/cjia/ch06_xx.clj"] #object[java.io.File 0x6a452f90 "./src/clojure/clojure_walkthrough/cjia/ch09_xx.clj"] #object[java.io.File 0x5d5e66e0 "./src/clojure/clojure_walkthrough/cjia/ch08_object.clj"] #object[java.io.File 0x4683ac5 "./src/clojure/clojure_walkthrough/cjia/ch03_05.clj"] #object[java.io.File 0x10b4f792 "./src/clojure/clojure_walkthrough/cjia/ch08_xx.clj"] #object[java.io.File 0x53ce63bb "./src/clojure/clojure_walkthrough/cjia/ch03_04.clj"] #object[java.io.File 0x7d096644 "./src/clojure/clojure_walkthrough/cjia/ch03_07.clj"] #object[java.io.File 0x3872d532 "./src/clojure/clojure_walkthrough/cjia/ch07_xx.clj"] #object[java.io.File 0xfcdbdc9 "./src/clojure/clojure_walkthrough/cjia/ch03_03.clj"] #object[java.io.File 0x2f3ec430 "./src/clojure/clojure_walkthrough/cjia/readme.md"] #object[java.io.File 0x19b3acd5 "./src/clojure/clojure_walkthrough/cjia/ch10_xx.clj"] #object[java.io.File 0x37c63194 "./src/clojure/clojure_walkthrough/core.clj"])

;; 4.9: reading and writing text files

(spit "./foo.txt" "my stuff\n and more")

;; read it back
(slurp "./foo.txt") ;; "my stuff\n and more"

;; use of encoding as required
(slurp "./foo.txt" :encoding "UTF-8") ;;

;; append data to existing file
(spit "./foo.txt" "\neven more stuff" :append true)

(slurp "./foo.txt") ;; "my stuff\n and more\neven more stuff"

;; read file line by line
(with-open [r (clojure.java.io/reader "./foo.txt")]
  (doseq [line (line-seq r)]
    (println line)))

(defn spitn
  "Append to file with newline"
  [path text]
  (spit path (str text "\n") :append true))

;; 04.10: using temp file
(def my-temp-file (java.io.File/createTempFile "filename" ".txt"))

;; write to temp file like normally
(with-open [file (clojure.java.io/writer my-temp-file)]
  (binding [*out*  file]
    (println "Example output.")))

(.getAbsolutePath my-temp-file) ;; "/tmp/filename6251207477218285160.txt"

(.deleteOnExit my-temp-file)

(.delete my-temp-file)

;; 04.11: reading/writing files at arbitrary positions
(import '[java.io RandomAccessFile])

;; Make a 1 GB file filled with zeros except the integer 1,234 at the end
(doto (RandomAccessFile. "/tmp/longfile" "rw")
  (.seek (* 1000 1000 1000))
  (.writeInt 1234)
  (.close))

(require '[clojure.java.io :refer [file]])
(.length (file "/tmp/longfile")) ;; 1000000004

(let [raf (RandomAccessFile. "/tmp/longfile" "r")
      _ (.seek raf (* 1000 1000 1000))
      result (.readInt raf)]
  (.close raf)
  result) ;; 1234

;; 04.12: Parallelizing file processing

(require ['clojure.java.io :as 'jio])

(defn pmap-file
  "Process input-file in parallel, applying processing-fn to each row outputting into output-file"
  [processing-fn input-file output-file]
  (with-open [rdr (jio/reader input-file)
              wtr (jio/writer output-file)]
    (let [lines (line-seq rdr)]
      (dorun
       (map #(.write wtr %)
            (pmap processing-fn lines))))))

;; Example of calling this function
(def accumulator (atom 0))

(defn- example-row-fn
  "Trivial example"
  [row-string]
  (str row-string "," (swap! accumulator inc) "n"))

;; Call it
(pmap-file example-row-fn "./foo.txt" "./bar.txt")

;; 04.13: Parallizing file processing with reducers (TBC)
