(defproject clojure-walkthrough "0.1.0-SNAPSHOT"
  :description "Clojure By Examples"
  :url "https://github.com/agilecreativity/clojure-walkthrough"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/data.json "2.5.1"]
                 [org.clojars.hozumi/clj-commons-exec "1.2.0"]
                 [org.clojure/core.match "1.1.0"]
                 [iota "1.1.3"]
                 [jline "0.9.94"]]
  ;:main ^:skip-aot clojure-walkthrough.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :source-paths ["src/clojure" "test/clojure"]
  ;; Add more if you like
  :resource-paths ["resources" "src/resources"]
  :java-source-paths ["src/java" "test/java"]
  :aot [com.gentest.genclojure]
  :main com.gentest.ConcreteClojureClass)
