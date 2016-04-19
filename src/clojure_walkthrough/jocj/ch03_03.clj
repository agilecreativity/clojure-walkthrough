(ns clojure-walkthrough.jocj.ch03-03
  (use clojure.pprint)
  (:gen-class))

;; 3.3.4: writing high-order functions

(def users
  [{:username "kyle"
    :firstname "Kyle"
    :lastname   "Smith"
    :balance    175.00M  ; Use BigDecimals for money
    :member-since "2009-04-16"
    }
   {:username "zak"
    :firstname "Jones"
    :lastname   "Zackery"
    :balance    12.95M
    :member-since  "2009-02-01"
    }
   {:username   "rob"
    :firstname  "Robert"
    :lastname   "Jones"
    :balance    98.50M
    :member-since "2009-03-30"}])

(defn sorter-using [ordering-fn]
  (fn [collection]
    (sort-by ordering-fn collection)))

(defn lastname-firstname [user]
  [(user :lastname) (user :firstname)])

(defn balance [user]
  (user :balance))

(defn username [user]
  (user :username))

(def poorest-first (sorter-using balance))
(def alphabetically (sorter-using username))
(def last-then-firstname (sorter-using lastname-firstname))

;; Let's see some result
(map username users)  ;; ("kyle" "zak" "rob")
(sort *1) ;; ("kyle" "rob" "zak") ;; Note: *1 means 'last result return by REPL'

(pprint  (sort-by username users)) ;; =>
;; ({:username "kyle",
;;  :firstname "Kyle",
;;  :lastname "Smith",
;;  :balance 175.00M,
;;  :member-since "2009-04-16"}
;; {:username "rob",
;;  :firstname "Robert",
;;  :lastname "Jones",
;;  :balance 98.50M,
;;  :member-since "2009-03-30"}
;; {:username "zak",
;;  :firstname "Jones",
;;  :lastname "Zackery",
;;  :balance 12.95M,
;;  :member-since "2009-02-01"})

(def poorest-first (sorter-using balance))
(poorest-first users) ;; ({:username "zak", :firstname "Jones", :lastname "Zackery", :balance 12.95M, :member-since "2009-02-01"} {:username "rob", :firstname "Robert", :lastname "Jones", :balance 98.50M, :member-since "2009-03-30"} {:username "kyle", :firstname "Kyle", :lastname "Smith", :balance 175.00M, :member-since "2009-04-16"})

;; Note: the two result are the same!
(defn poorest-first [users] (sort-by balance users))
(poorest-first users) ;; ({:username "zak", :firstname "Jones", :lastname "Zackery", :balance 12.95M, :member-since "2009-02-01"} {:username "rob", :firstname "Robert", :lastname "Jones", :balance 98.50M, :member-since "2009-03-30"} {:username "kyle", :firstname "Kyle", :lastname "Smith", :balance 175.00M, :member-since "2009-04-16"})

(map lastname-firstname users) ;; (["Smith" "Kyle"] ["Zackery" "Jones"] ["Jones" "Robert"])
(sort *1) ;; (["Jones" "Robert"] ["Smith" "Kyle"] ["Zackery" "Jones"])

(last-then-firstname users) ;; ({:username "rob", :firstname "Robert", :lastname "Jones", :balance 98.50M, :member-since "2009-03-30"} {:username "kyle", :firstname "Kyle", :lastname "Smith", :balance 175.00M, :member-since "2009-04-16"} {:username "zak", :firstname "Jones", :lastname "Zackery", :balance 12.95M, :member-since "2009-02-01"})

;; 3.3.5: Anonymous functions

(def total-cost
  (fn [item-cost number-of-items]
    (* item-cost number-of-items)))

;; Anonymous function
(map (fn [user] (user :member-since)) users) ;; ("2009-04-16" "2009-02-01" "2009-03-30")

;; Using shortcuts, produce the same result as above
(map #(% :member-since) users) ;; ("2009-04-16" "2009-02-01" "2009-03-30")

;; More examples
(#(vector %&) 1 2 3 4 5) ;; [(1 2 3 4 5)]
(#(vector % %&) 1 2 3 4 5) ;; [1 (2 3 4 5)]
(#(vector %1 %2 %&) 1 2 3 4 5) ;; [1 2 (3 4 5)]
(#(vector %1 %2 %&) 1 2) ;; [1 2 nil]

;; 3.3.6: Keywords and symbols

(def person {:username "zak"
             :balance 12.95
             :member-since "2009-02-01"})

(person :username) ;; "zak"

(:username person) ;; "zak" ;; same result

;; consider the following
(map #(% :member-since) users) ;; ("2009-04-16" "2009-02-01" "2009-03-30")

; clearer when using the keyword as a function
(map :member-since users)      ;; ("2009-04-16" "2009-02-01" "2009-03-30")

(:login person) ;; nil

(:login person :not-found) ;; :not-found

;; Use of Symbols

(def expense {'name "Snow Leopard" 'cost 29.95M})

(expense 'name) ;; "Snow Leopard"

('name expense) ;; "Snow Leopard"

('vendor expense) ;; nil

('vendor expense :absent) ;; :absent

(person :username) ;; "zak"

(person :login :not-found) ;; :not-found

;; Note: use of vectors
(def names ["kylie" "zak" "rob"])

(names 1) ;; "zak"

;; invalid index
(names 10) ;; exception IndexOutOfBoundException

(names 10 :not-found) ;; Arity exception
