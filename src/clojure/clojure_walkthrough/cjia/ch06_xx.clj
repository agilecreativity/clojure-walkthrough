(ns clojure-walkthrough.cjia.ch06_xx
  (:require [clojure.data.json :as json]
            [clojure.xml :as xml-core])
  (:gen-class))

;; 6.4.1: creating refs
(def all-users (ref {}))

(deref all-users) ;; {}

@all-users ;; {}

all-users ;; #ref[{:status :ready, :val {}} 0x7c93b91f]

;; 6.4.2: mutating refs with ref-set
;(ref-set all-users {}) ; IllegalStateException

(dosync
 (ref-set all-users {})) ;; {}

;; Alter: (alter ref function & args)

(defn new-user [id login monthly-budget]
  {:id id
   :login login
   :monthly-budget monthly-budget
   :total-expensives 0})

;; Let's add something to this user
(defn add-new-user [login budget-amount]
  (dosync
   (let [current-number (count @all-users)
         user (new-user (inc current-number) login budget-amount)]
     (alter all-users assoc login user))))

(add-new-user "amit" 100000) ;; {"amit" {:id 2, :login "amit", :monthly-budget 100000, :total-expensives 0}}

(add-new-user "deepthi" 200000) ;; {"amit" {:id 2, :login "amit", :monthly-budget 100000, :total-expensives 0}, "deepthi" {:id 3, :login "deepthi", :monthly-budget 200000, :total-expensives 0}}

;; 6.5 Agents
(def total-cpu-time (agent 0))

(deref total-cpu-time) ;; 0

@total-cpu-time ;; 0

;; Mutating agents
; (send the-agent the-function & more-args)

(send total-cpu-time + 700) ;; #agent[{:status :ready, :val 1400} 0x76768bd0]

(deref total-cpu-time) ;; 700

;; Send off: (send-off the-agent the-function & more-args)

;; 6.5.3: Working with agents
; await and wait-for: (await & the-agents)

;; Agent errors
(def bad-agent (agent 10))

;(send bad-agent / 0) ;; divided by zero

(deref bad-agent)  ;; 10

;(send bad-agent / 2) ;; still get arror

(agent-error bad-agent)

(let [e (agent-error bad-agent)
      st (.getStackTrace e)]
  (println (.getMessage e))
  (println (clojure.string/join "\n" st)))

(clear-agent-errors bad-agent) ;; agent is now ready for more actions

;; 6.6: Atoms
(def total-rows (atom 0))

(deref total-rows) ;; 0

; or the same as
@total-rows

;; 6.6.2: mutating atoms
; (reset! atom new-value)
; (swap! the-atom the-function & more-args)

(swap! total-rows + 100)
@total-rows ;; 100

;; compare-and-set!
;(compare-and-set! the-atom old-value new-value)

;; 6.7: Vars
(def hbase-master "localhost")

(def ^:dynamic *hbase-master* "localhost")

(str "Hbase-master is:" *hbase-master*) ;; "Hbase-master is:localhost"

;; If you use without root binding you will get unbound object
(def ^:dynamic *rabbitmq-host*)

(str "RabbitMQ host is:" *rabbitmq-host*) ;; "RabbitMQ host is:Unbound: #'clojure-walkthrough.cjia.ch06_xx/*rabbitmq-host*"

(bound? #'*rabbitmq-host*) ;; false

;; 6.7.2: Var bindings

(def ^:dynamic *mysql-host*)

(defn db-query [db]
  (binding [*mysql-host* db]
    (count *mysql-host*)))

(def mysql-hosts ["test-mysql" "dev-mysql" "staging-mysql"])

(pmap db-query mysql-hosts) ;; (10 9 13)

;; 6.8: State and its unified access model (TBC)
;; creating
(def a-ref (ref 0))
(def an-agent (agent 0))
(def an-atom (atom 0))

;; reading
(deref a-ref)    ; or @a-ref
(deref an-agent) ; or @an-agent
(deref an-atom)  ; or @an-atom

;; Mutation (for refs)
;(ref-set new-value)
;(alter ref function & args)
;(commut ref function & args)

;; Mutation (for agents)
;(send agent function & args)
;(send-off agent function & args)

;; Mutattion (for atoms)
;(reset! atom new-value)
;(swap! atom function & args)
;(compare-and-set! atom old-value new-value)

;; 6.8.5: watching for mutation with `add-watch` and `remove-watch`
(def adi (atom 0))

(defn on-change [the-key the-ref old-value new-value]
  (println "Hey, seeing change from " old-value " to " new-value))

; Use of add-watch function
(add-watch adi :adi-watcher on-change)

(deref adi) ;

(swap! adi inc) ; 1
(swap! adi inc) ; 2

; we can remove them using `remove-watch`
(remove-watch adi :adi-watcher)

;; 6.10: Futures and promises

(defn long-calculation [num1 num2]
  ; Simulated the long running process
  (Thread/sleep 5000)
  (* num1 num2))

(defn long-run []
  (let [x (long-calculation 11 13)
        y (long-calculation 13 17)
        z (long-calculation 17 19)]
    (* x y z)))

(time (long-run)) ;; "Elapsed time: 15008.324 msecs" and return 10207769

;; Now let's use future
(defn fast-run []
  (let [x (future (long-calculation 11 13)) ; future creates a thread that will run
        y (future (long-calculation 13 17)) ; long-calculation without blocking current thread
        z (future (long-calculation 17 19))]
    (* @x @y @z))) ;; Futures x, y, and z can potentially all run in parallel

(time (fast-run)) ;; "Elapsed time: 5006.027 msecs" and return 10207769

;; Here are other functions that Clojure provides:
; future?, future-done?, future-cancel?, future-cancelled?
