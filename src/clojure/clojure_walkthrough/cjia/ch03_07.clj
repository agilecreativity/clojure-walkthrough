;; Note: replace ch03-xx the right number
(ns clojure-walkthrough.cjia.ch03-07
  (:import java.util.UUID) 
  (:require [clojure.data.json :as json]
            [clojure.xml :as xml-core])
  (:gen-class))

(defn guid [four-letters-four-digits]
  (java.util.UUID/fromString (str four-letters-four-digits "-1000-413f-8a7a-f11c6a9c4036")))

(guid "abcd1234") ;; #uuid "abcd1234-1000-413f-8a7a-f11c6a9c4036"
