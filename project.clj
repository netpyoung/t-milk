(defproject t-milk "0.1.0-SNAPSHOT"

  :dependencies
  [[org.clojure/clojure "1.6.0"]
   [org.clojure/clojurescript "0.0-2227"]
   [org.clojure/core.async "0.1.303.0-886421-alpha"]

   [jayq "2.5.1"]
   ]

  :plugins
  [[lein-cljsbuild "1.0.3"]]

  :clean-targets ^{:protect false}
  [:target-path "out" "resources/public/t-milk.js" "resources/public/out"]

  :profiles
  {:dev
   {:dependencies
    [[ring "1.2.2"]
     [compojure "1.1.8"]
     [com.cemerick/piggieback "0.1.3"]
     [weasel "0.2.0"]]

    :source-paths
    ["dev/clj"]

    :repl-options
    {:init-ns repl
     :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
     :init (run!)}

    :cljsbuild
    {:builds
     [{:id "dev"
       :source-paths ["src" "dev/cljs"]
       :compiler {
                  :output-to "resources/public/t-milk.js"
                  :output-dir "resources/public/out/"
                  :optimizations :none
                  :static-fns false
                  :source-map true}}]
     }
    }

   }
  )
