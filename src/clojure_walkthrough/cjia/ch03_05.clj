(ns clojure-walkthrough.cjia.ch03-05
  (:require [clojure.data.json :as json]
            [clojure.xml :as xml-core])
  (:gen-class))

;; See: https://github.com/clojure/data.json
(def sample-json
  (json/write-str {:a 1 :b 2}))

sample-json ;; "{\"a\":1,\"b\":2}"

(json/read-str sample-json) ;; {"a" 1, "b" 2}

;;  e.g. 3.8:
(declare load-totals)

(defn import-xml-transactions-from-bank [url]
  (let [xml-document (xml-core/parse url)]
    ;; more code here
    ))

(defn totals-by-day [start-date end-date]
  (let [expenses-by-day (load-totals start-date end-date)]
    (json/json-str expense-by-day)))
