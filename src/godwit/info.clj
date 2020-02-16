(ns godwit.info
  
  (:require 
    [godwit.log :as log]
    [taoensso.timbre :as timbre])
  
  (:gen-class))

(defn run_info
  "Reports information on XML files"
  [{:keys [verbose debug files]}]

  ;; Configure Logging
  (log/config verbose debug)
  (timbre/info "Called information operation")

  )
