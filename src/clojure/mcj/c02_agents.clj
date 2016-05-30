(ns mcj.c02-agents)

(def state (agent {}))

(defn set-value-in-ms [n ms]
  "Returns a closure which sleeps and calls assoc"
  (fn [a]
    (Thread/sleep ms)
    (assoc a :value n)))

(send state (set-value-in-ms 5 5000))

(send-off state (set-value-in-ms 10 5000)) 

@state

(send-off state (set-value-in-ms 100 3000))

@state

(await state) ;; will block

(def a (agent 1))

(send a / 0)
(agent-error a) ;; 
;; #error
;; {
 ;; :cause "Divide by zero"
 ;; :via
 ;; [{:type java.lang.ArithmeticException
 ;;   :message "Divide by zero"
 ;;   :at [clojure.lang.Numbers divide "Numbers.java" 158]}]...

(clear-agent-errors a) ;;

;; Error will be cleared, thus we can deref a again
@a ;; 1

(def pool (java.util.concurrent.Executors/newFixedThreadPool 10))
(send-via pool state assoc :value 1000)
@state ;; {:value 1000}

;; Implement dining philosopher problem using agents. (TBC)
