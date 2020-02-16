(ns godwit.core
  (:require 
    ;; Module Imports
    [cli-matic.core :refer [run-cmd]]
    [clojure.string :refer [join]]

    ;; Local Imports
    [godwit.info])
(:gen-class)
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Run Version
(def vers "0.1.0")
(defn run_version
  "Reports version information to stdout"
  [{:keys [verbose]}]
  (if verbose
    (println 
      (join "\n  " ["Godwit - The Document Processor" 
                    "Kenneth P. J. Dyer <kenneth@avoceteditors.com>"
                    "Avocet Editorial Consulting"
                    (join " " ["Version" vers])])
             )
    (println "Godwit - version" vers)
    
    )

  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CLI Configuration
(def CONFIG
  
  {:app   {:command "gwt"
           :description "The Godwit XML Document Processor"
           :type boolean
           :default false}
   :global-opts [
                ;; Enables debugging messages in output 
                {:option "debug"
                 :short "d"
                 :as "Enables debugging in output messages" 
                 :type :with-flag}

                ;; Forces certain operations
                {:option "force" 
                 :short "f"
                 :as "Forces certain operations"
                 :type :with-flag}

                ;; Enables Verbosity in CLI output
                {:option "verbose" 
                 :short "v"
                 :as "Enables verbosity in output message"
                 :type :with-flag}
                ]
   :commands [
              ;; Information Command (used to report general info on XML files)
              {:command "info"
               :description ["Reports information on XML file" "" ""]
               :options [{:option "file" 
                          :short "F"
                          :type :string 
                          :default "project.xml"
                          :as "Specifies the file to read"}] 
               :runs godwit.info/run_info}

              ;; Version Command (Used to report release number) 
              {:command "version"
               :description ["Reports version information" "" ""]
               :runs run_version
               }
              ]
   }
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Process
(defn -main 
  "Main process for Godwit"
  [& args]
  (run-cmd args CONFIG)
  )
