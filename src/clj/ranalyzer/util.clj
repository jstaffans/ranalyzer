(ns ranalyzer.util
  (:require [clojure.core.async :as async]))

(defn clear-keys
  "dissoc for components records. assoc's nil for the specified keys"
  [m & ks]
  (apply assoc m (interleave ks (repeat (count ks) nil))))

(defn close-channels [component & ks]
  (doseq [k ks]
    (when-let [ch (get component k)]
      (async/close! ch)))
  (apply clear-keys component ks))
