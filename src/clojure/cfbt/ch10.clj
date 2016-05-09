(ns cfbt.ch10)

(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorate 0}))

@fred ;; {:cuddle-hunger-level 0, :percent-deteriorate 0}

(let [zombie-state @fred]
  (if (>= (:percent-deteriorate zombie-state) 50)
    (future (println (:percent-deteriorate zombie-state)))))

(swap! fred
       (fn [current-state]
         (merge-with + current-state {:current-hunger-level 1})))
;; {:cuddle-hunger-level 0, :percent-deteriorate 0, :current-hunger-level 3}
@fred
;; {:cuddle-hunger-level 0, :percent-deteriorate 0, :current-hunger-level 3}
(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 2
                                      :percent-deteriorated 1})))

@fred

(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1
                                      :percent-deteriorated 1})))

(defn increase-cuddle-hunger-level
  [zombie-state increase-by]
  (merge-with + zombie-state {:cuddle-hunger-level increase-by}))

;; No update will be applied without the use of swap
(increase-cuddle-hunger-level @fred 10)

@fred

(swap! fred increase-cuddle-hunger-level 10)

@fred

;; use Clojure built-in
(update-in {:a {:b 3}} [:a :b] inc)
;; {:a {:b 4}}

(update-in {:a {:b 3}} [:a :b] + 10)
;; {:a {:b 13}}

(swap! fred update-in [:cuddle-hunger-level] + 10)

(let [num (atom 1)
      s1 num]
  (swap! num inc)
  (println "State 1:" s1)
  (println "Current state:" @num))

(reset! fred {:cuddle-hungerlevel 0
              :percent-deteriorated 0})
;; {:cuddle-hungerlevel 0, :percent-deteriorated 0}

;; Watches and Validators ::
(defn shuffle-speed
  [zombie]
  (* (:cuddle-hunger-level zombie)
     (- 100 (:percent-deteriorated zombie))))

(defn shuffle-alert
  [key watched old-state new-state]
  (let [sph (shuffle-speed new-state)]
    (if (> sph 5000)
      (do
        (println "Run, you fool!")
        (println "The zombie's SPH is now " sph)
        (println "This message brought to you courtesy of " key))
      (do
        (println "All's well with " key)
        (println "Cuddle hunger: " (:cuddle-hunger-level new-state))
        (println "Percent deteriorated: " (:percent-deteriorated new-state))
        (println "SPH: " sph)))))

(reset! fred {:cuddle-hunger-level 22
              :percent-deteriorated 2})
;; {:cuddle-hunger-level 22, :percent-deteriorated 2}
(add-watch fred :fred-shuffle-alert shuffle-alert)
(swap! fred update-in [:percent-deteriorated] + 2)
(swap! fred update-in [:cuddle-hunger-level] + 30)

;; Validators ::

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (and (>= percent-deteriorated 0)
       (<= percent-deteriorated 100)))

;; Use validator at creation time
(def boby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
    :validator percent-deteriorated-validator))

;;(swap! boby update-in [:percent-deteriorated] + 200)
;; => Throw "Invalid reference state!"

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (or (and (>= percent-deteriorated 0)
           (<= percent-deteriorated 100))
      (throw (IllegalStateException. "That's not mathly"))))

(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator))

;;(swap! bobby update-in [:percent-deteriorated] + 200)

;; Refs ::

(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
    "passive-aggressive" "striped" "polka-dotted"
    "athletic" "business" "power" "invisible" "gollumed"})

(defn sock-count
  [sock-variety count]
  {:variety sock-variety
   :count count})

(defn generate-sock-gnome
  "Create an initial sock gnome state with no socks"
  [name]
  {:name name
   :socks #{}})

;; now create the ref
(def sock-gnome (ref (generate-sock-gnome "Barumpharumph")))

(def dryer (ref {:name "LG 1337"
                 :socks (set (map #(sock-count % 2) sock-varieties))}))

(:socks @dryer) ;;
;; #{{:variety "gollumed", :count 2} {:variety "striped", :count 2} {:variety "wool", :count 2} {:variety "passive-aggressive", :count 2} {:variety "argyle", :count 2} {:variety "business", :count 2} {:variety "darned", :count 2} {:variety "polka-dotted", :count 2} {:variety "horsehair", :count 2} {:variety "power", :count 2} {:variety "athletic", :count 2} {:variety "mulleted", :count 2} {:variety "invisible", :count 2}}

(defn steal-sock
  [gnome dryer]
  (dosync
   (when-let [pair (some #(if (= (:count %) 2) %) (:socks @dryer))]
     (let [updated-count (sock-count (:variety pair) 1)]
       (alter gnome update-in [:socks] conj updated-count)
       (alter dryer update-in [:socks] disj pair)
       (alter dryer update-in [:socks] conj updated-count)))))

(steal-sock sock-gnome dryer)
;; {:name "LG 1337", :socks #{{:variety "striped", :count 2} {:variety "wool", :count 2} {:variety "passive-aggressive", :count 2} {:variety "argyle", :count 2} {:variety "business", :count 2} {:variety "darned", :count 2} {:variety "polka-dotted", :count 2} {:variety "horsehair", :count 2} {:variety "power", :count 2} {:variety "athletic", :count 2} {:variety "gollumed", :count 1} {:variety "mulleted", :count 2} {:variety "invisible", :count 2}}}

(:socks @sock-gnome)
;; #{{:variety "gollumed", :count 1}}

(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))

(similar-socks (first (:socks @sock-gnome)) (:socks @dryer))
;; ({:variety "gollumed", :count 1})

(def counter (ref 0))
(future
  (dosync
   (alter counter inc)
   (println @counter)
   (Thread/sleep 500)
   (alter counter inc)
   (println @counter)))

(Thread/sleep 250)
(println @counter)

;; Commute ::
(defn sleep-print-update
  [sleep-time thread-name update-fn]
  (fn [state]
    (Thread/sleep sleep-time)
    (println (str thread-name ": " state))
    (update-fn state)))

(def counter (ref 0))

(future (dosync
         (commute counter (sleep-print-update 100 "Thread A" inc))))

(future (dosync
         (commute counter (sleep-print-update 105 "Thread B" inc))))

;; Vars ::
(def ^:dynamic *notification-address* "dobby@elf.org")

(binding
 [*notification-address* "test@elf.org"]
  (str "address: " *notification-address*)) ;; "address: test@elf.org"

(binding [*notification-address* "tester-1@elf.org"]
  (str "address: " *notification-address*)) ;; "address: tester-1@elf.org"

(str "address: " *notification-address*) ;; "address: dobby@elf.org"

(defn notify
  [message]
  (str "TO: " *notification-address* "\n"
       "MESSAGE: " message))

(notify "I fell.")
;;=> "TO: dobby@elf.org\nMESSAGE: I fell."

;; Use binding
(binding [*notification-address* "test@elf.org"]
  (notify "test!"))
;; => "TO: test@elf.org\nMESSAGE: test!"

(binding [*out* (clojure.java.io/writer "print-output")]
  (println "A man who carries a cat by the tail learns something he can learn in no other way. -- Mark Twain"))

(slurp "print-output")
;; => "A man who carries a cat by the tail learns something he can learn in no other way. -- Mark Twain\n"

(def ^:dynamic *troll-thought* nil)
(defn troll-riddle
  [your-answer]
  (let [number "man meat"]
    (when (thread-bound? #'*troll-thought*)
      (set! *troll-thought* number))
    (if (= number your-answer)
      "TROLL: You can cross the bridge!"
      "TROLL: Time to eat you, succulent human!")))

(binding [*troll-thought* nil]
  (println (troll-riddle 2))
  (println "SUCCULENT HUMAN: Ooooh! The answer was" *troll-thought*))
;; => TROLL: Time to eat you, succulent human!
;; => SUCCULENT HUMAN: Ooooh! The answer was man meat

;; Per-Thread Binding
(.write *out* "prints to repl")

(.start) (Thread. #(.write *out* "prints to standard out"))

(let [out *out*]
  (.start
   (Thread. #(binding [*out* out]
               (.write *out* "prints to repl from thread")))))


(.start (Thread. (bound-fn [] (.write *out* "print to repl from thread!"))))

;; Altering the Var Root

(def power-source "hair")

(alter-var-root #'power-source (fn [_] "7-eleven parking lot"))

power-source ;; "7-eleven parking lot"

(with-redefs [*out* *out*]
  (doto (Thread. #(println "with redefs allow me to show up in the REPL"))
    .start
    .join))

;; pmap examples
(defn always-1
  []
  1)

(take 5 (repeatedly always-1)) ;; (1 1 1 1 1)

;; lazy sequence of random numbers between 0 and 9
(take 5 (repeatedly (partial rand-int 10))) ;; (0 5 8 4 9)

;; compare the performance of pmap and map
(def alphabet-length 26)

;; Vector of chars, A-Z
(def letters (mapv (comp str char (partial + 65)) (range alphabet-length)))

(defn random-string
  "Returns a random string of specified length"
  [length]
  (apply str (take length (repeatedly #(rand-nth letters)))))

(defn random-string-list
  [list-length string-length]
  (doall (take list-length (repeatedly (partial random-string string-length)))))

(def orc-names (random-string-list 3000 7000))

(time (dorun (map clojure.string/lower-case orc-names)))
;; Elapsed time: 320.863699 msec

(time (dorun (pmap clojure.string/lower-case orc-names)))
;; Elapsed time: 184.969880 msec

(def orc-name-abbrevs (random-string-list 20000 300))

(time (dorun (map clojure.string/lower-case orc-name-abbrevs)))

;; Elapsed time: 151.107737 msec

(time (dorun (pmap clojure.string/lower-case orc-name-abbrevs)))
;; Elapsed time: 109.60039 msec

(def numbers [1 2 3 4 5 6 7 8 9 10])

(partition-all 3 numbers) ;; ((1 2 3) (4 5 6) (7 8 9) (10))
(pmap inc numbers) ;; (2 3 4 5 6 7 8 9 10 11)

(pmap (fn [number-group] (doall (map inc number-group)))
      (partition-all 3 numbers)) ;; ((2 3 4) (5 6 7) (8 9 10) (11))

(apply concat
      (pmap (fn [number-group] (doall (map inc number-group)))
            (partition-all 3 numbers))) ;; (2 3 4 5 6 7 8 9 10 11)

(time
 (dorun
  (apply concat
         (pmap (fn [name] (doall (map clojure.string/lower-case name)))
               (partition-all 1000 orc-name-abbrevs)))))
;; Elapsed time: 84.826032 msecs

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel overhead worthwhile"
  [grain-size f & colls]
  (apply concat
         (apply pmap
                (fn [& pgroups] (doall (apply map f pgroups)))
                (map (partial partition-all grain-size) colls))))

(time (dorun (ppmap 1000 clojure.string/lower-case orc-name-abbrevs)))
;; "Elapsed time: 67.655845 msecs"
