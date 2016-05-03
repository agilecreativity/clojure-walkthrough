(ns clojure.cfbt.ch05)

(defn wisdom
  [words]
  (str words ", Daniel-san"))

(wisdom "Always bathe on Friday") ;; "Always bathe on Friday, Daniel-san"

;; non-pure functional
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)
    ("You get a raise!"
     "Better luck next year!")))

(defn analysis [text]
  (str "Character count: " (count text)))

(defn analyze-file
  [filename]
  (analysis (slurp filename)))

(analyze-file "project.clj") ;; "Character count: 659"

;; side-effect-free example

(def great-baby-name "Rosanthony")

(let [great-baby-name "Bloodthunder"]
  great-baby-name) ;; "Bloodthunder"

great-baby-name ;;  "Rosanthony"

(defn sum
  ([vals] (sum vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (sum (rest vals) (+ (first vals) accumulating-total)))))

(sum [39 5 1]) ;; 45
(sum [39 5 1] 0) ;; 45
(sum [5 1] 39) ;; 45
(sum [] 45) ;; 45

;; Using recur
(defn sum
  ([vals]
   (sum vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (recur (rest vals) (+ (first vals) accumulating-total)))))

(sum [39 5 1]) ;; 45
(sum [5 1] 39) ;; 45

(require '[clojure.string :as s])
(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(clean "My boa constrictor is so sassy lol!  ") ;; "My boa constrictor is so sassy LOL!"

;; Use comp
((comp inc *) 2 3) ;; 7

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})

(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength     :attributes))
(def c-dex (comp :dexterity    :attributes))

(c-int character) ;; 10
(c-str character) ;; 4
(c-dex character) ;; 5

(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))

(spell-slots character) ;; 6

;; do the same with `comp`

(def spell-slots-comp (comp int inc #(/ % 2) c-int))

(spell-slots-comp character) ;; 6

(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

;; Memoize
(+ 3 (+ 5 8)) ;; 16
(+ 3 (13))    ;; 15

(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 1000)
  x)

(sleepy-identity "Mr. Fantastico") ;; "Mr. Fantastico"

(def memo-sleepy-identity (memoize sleepy-identity))

(memo-sleepy-identity "Mr. Fantastico") ;; "Mr. Fantastico"

;; The PEG things (see the official source)
;; See: https://github.com/flyingmachine/pegthing
