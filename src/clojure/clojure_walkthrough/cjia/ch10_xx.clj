(ns clojure-walkthrough.cjia.ch10-xx
  (:require [clojure.test :refer :all]
            [clojure-walkthrough.cjia.ch10-date-operations :refer :all]))

(deftest test-simple-data-parsing
  (let [d (date "2009-01-22")]
    (is (= (day-from d) 22))))
