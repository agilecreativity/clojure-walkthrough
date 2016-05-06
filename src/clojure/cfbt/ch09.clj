(ns cfbt.ch09
  (:require [clojure.core.async :refer [timeout]]))

(future (Thread/sleep 4000)
        (println "I'll print after 4 seconds"))
(println "I'll print immediately")

(let [result (future (println "This prints once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  (println "@:     " @result))
;; => This print once
;; => deref: 2
;; => @: 2

(let [result (future (Thread/sleep 3000)
                     (+ 1 1))]
  (println "The result is: " @result)
  (println "It will be at least 3 seconds before I print"))

;; => The result is: 2
;; => It will be at least 3 seconds before I print

;; return the value 5, if the future doesn't return a value after 10 milliseconds
(deref (future (Thread/sleep 1000) 0) 10 5) ;; 5

;; Interrogate a future with `realized?`
(realized? (future (Thread/sleep 1000))) ;; false

(let [f (future)]
  @f
  (realized? f))  ;; true

;; Delays::
(def jackson-5-delay
  (delay (let [message "Just call me and I'll be there"]
           (println "First deref:" message)
           message))) ;; nothing printed!

(force jackson-5-delay)
;; => First deref: Just call me and I'll be there
;; => "Just call me and I'll be there"

(def gimli-headshots ["serious.jpg" "fun.jpg" "playful.jpg"])

(defn email-user
  [email-address]
  (println "Sending headshot notification to" email-address))

(defn upload-document
  "Needs to be implemented"
  [headshot]
  true)

(let [notify (delay (email-user "add-my-axe@gmail.com"))]
  (doseq [headshot gimli-headshots]
    (future (upload-document headshot)
            (force notify))))

;; Promises::
(def my-promise (promise))
(deliver my-promise (+ 1 2))
@my-promise ;; 3

(def yak-butter-international
  {:store "Yak Butter International"
   :price 90
   :smoothness 90})

(def butter-than-nothing
  {:store "Butter Than Nothing"
   :price 150
   :smoothness 83})

;; This is the butter that meets our requirements
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  "If the butter meets our criteria, return the butter, else return false"
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

(time (some (comp satisfactory? mock-api-call)
            [yak-butter-international butter-than-nothing baby-got-yak]))
;; => "Elapsed time: 3001.095871 msecs"
;; => {:store "Baby Got Yak", :price 94, :smoothness 99}

(time
 (let [butter-promise (promise)]
   (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
     (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
               (deliver butter-promise satisfactory-butter))))
   (println "And the winner is:" @butter-promise)))
;; => And the winner is: {:store Baby Got Yak, :price 94, :smoothness 99}
;; "Elapsed time: 1002.367831 msecs"

;; To avoid lock in some situation
(let [p (promise)]
  (deref p 100 "timed out")) ;; "timed out"

(let [ferengi-wisdom-promise (promise)]
  (future (println "Here's some Fereng wisdom:" @ferengi-wisdom-promise))
  (Thread/sleep 100)
  (deliver ferengi-wisdom-promise "Whisper your way to success."))
;; => Here's some Fereng wisdom:
;; =>  Whisper your way to success.

;; Rolling Your Own Queue::
(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(let [saying3 (promise)]
  (future (deliver saying3 (wait 100 "Cheerio")))
  @(let [saying2 (promise)]
     (future (deliver saying2 (wait 400 "Pip pip!")))
     @(let [saying1 (promise)]
        (future (deliver saying1 (wait 200 "Ello, gov'na!"))))))

;; => "Ello, gov'na!"

(defmacro enqueue
  ([q concurrent-promise-name concurrent serialized]
   `(let [~concurrent-promise-name (promise)]
      (future (deliver ~concurrent-promise-name ~concurrent))
      (deref ~q)
      ~serialized
      ~concurrent-promise-name))
  ([concurrent-promise-name concurrent serialized]
   `(enqueue (future) ~concurrent-promise-name ~concurrent ~serialized)))

(time @(-> (enqueue saying (wait 200 "'Ello, gov'na!") (println @saying))
           (enqueue saying (wait 400 "Pip pip!") (println @saying))
           (enqueue saying (wait 100 "Cheerio!") (println @saying))))

;;=> 'Ello, gov'na!
;;=> Pip pip!
;;=> Cheerio!
;;=> "Elapsed time: 402.20443 msecs"
