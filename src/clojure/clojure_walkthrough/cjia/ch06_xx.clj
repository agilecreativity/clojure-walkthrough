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

