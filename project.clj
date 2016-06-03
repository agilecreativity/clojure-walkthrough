(defproject clojure-walkthrough "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojars.hozumi/clj-commons-exec "1.2.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [iota "1.1.1"]
                 [jline "2.11"]]
  ;:main ^:skip-aot clojure-walkthrough.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}}
  :source-paths ["src/clojure" "test/clojure"]
  ;; Add more if you like
  :resource-paths ["resources" "src/resources"]
  :java-source-paths ["src/java" "test/java"]
  :aot [com.gentest.genclojure]
  :main com.gentest.ConcreteClojureClass)
