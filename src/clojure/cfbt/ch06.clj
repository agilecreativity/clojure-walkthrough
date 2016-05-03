(ns cfbt.ch06)

(def great-books ["East of Eden" "The Glass Bead Game"])

great-books ;; ["East of Eden" "The Glass Bead Game"]

(ns-interns *ns*) ;; {great-books #'cfbt.ch06/great-books}
(get (ns-interns *ns*) 'great-books) ;; #'cfbt.ch06/great-books

(deref #'cfbt.ch06/great-books) ;; ["East of Eden" "The Glass Bead Game"]

great-books ;; ["East of Eden" "The Glass Bead Game"]

(def great-books ["The Power of Bees" "The Glass Bead Game"])

great-books ;; ["The Power of Bees" "The Glass Bead Game"]

;; Creating and switching to namespaces
(create-ns 'cheese.taxonomy) ;; #namespace[cheese.taxonomy]

(ns-name (create-ns 'cheese.taxonomy)) ;; cheese.taxonomy

;; Create a new namespace and switch to it
(in-ns 'cheese.analysis) ;; #namespace[cheese.analysis]

(in-ns 'cheese.taxonomy) ;; #namespace[cheese.taxonomy]
(def cheddars ["mild" "medium" "strong" "sharp" "extra sharp"]) ;; #'cheese.taxonomy/cheddars

(in-ns 'cheese.analysis) ;; #namespace[cheese.analysis]

;;cheese.analysis ;; => Exception..
cheese.taxonomy/cheddars ;; ["mild" "medium" "strong" "sharp" "extra sharp"]

;; Using of `refer`

(in-ns 'cheese.taxonomy)
(def chddars ["mild" "medium" "strong" "sharp" "extra sharp"])
(def bries ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"])
(in-ns 'cheese.analysis)
(clojure.core/refer 'cheese.taxonomy)
bries ;; ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
cheddars ;; ["mild" "medium" "strong" "sharp" "extra sharp"]
(clojure.core/get (clojure.core/ns-map clojure.core/*ns*) 'bries) ;; #'cheese.taxonomy/bries
(clojure.core/get (clojure.core/ns-map clojure.core/*ns*) 'cheddars) ;; #'cheese.taxonomy/cheddars

(clojure.core/refer 'cheese.taxonomy :only ['bries])
bries ;; ["Wisconsin" "Somerset" "Brie de Meaux"  "Brie de Melun"]

(in-ns 'cheese.analysis)
;; Notice the dash after "defn"
(defn- private-function
  "Just an example function that does nothing!"
  [])

(in-ns 'cheese.taxonomy)
(clojure.core/refer-clojure)
;(cheese.analysis/private-function) ;; Exception
;(refer 'cheese.analysis :only ['private-function]) ;; Exception..

