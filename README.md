# clojure-walkthrough

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar clojure-walkthrough-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Tips

- [Mix Java and Clojure files](https://github.com/technomancy/leiningen/blob/master/doc/MIXED_PROJECTS.md)

Make sure your `project.clj` have something like the following:

```clj
(defproject megacorp/superservice "1.0.0-SNAPSHOT"
  :description "A Clojure project with a little bit of Java sprinkled here and there"
  :min-lein-version  "2.0.0"
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"])
```

Current value for `project.clj`

```
(defproject clojure-walkthrough "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot clojure-walkthrough.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
 ;:source-paths ["src/clojure" "test/clojure"])
  :java-source-paths ["src/java" "test/java"])
```

```sh
# To compile Java file
lein javac

# To remove compilation artifacts
lein clean
```

### Useful Links

- [mixing-java-and-clojure](https://github.com/quephird/mixing-java-and-clojure)

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
