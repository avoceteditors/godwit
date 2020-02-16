(ns godwit.log
  (:require [taoensso.timbre :as timbre])
  (:gen-class))

(defn config
  [verbose debug]

  ;; Configre Logging Level
  (cond
    (and verbose debug)
      (timbre/set-level! :trace)
    (and (not verbose) debug) 
      (timbre/set-level! :debug)
    (and verbose (not debug))
      (timbre/set-level! :info)
    :else 
      (timbre/set-level! :warn)
  )
  (timbre/merge-config! 
    {:enabled? true
     :async? true})
)
