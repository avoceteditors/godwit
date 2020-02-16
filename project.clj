(defproject godwit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "BSD3"}
  :dependencies 
  [
   [org.clojure/clojure "1.10.1"]
   [bidi "2.1.3"]
   [ring/ring-defaults "0.3.1"]
   [com.taoensso/timbre "4.10.0"]
   [cli-matic "0.3.11"]
  ]
  :plugins [[lein-environ "0.4.0"]]
  :main godwit.core
  :jvm-opts ["-DClojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"
             ]
  :aot :all
  :repl-options {:init-ns godwit.core})

