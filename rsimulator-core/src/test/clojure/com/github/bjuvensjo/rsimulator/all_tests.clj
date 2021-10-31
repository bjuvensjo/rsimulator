(ns com.github.bjuvensjo.rsimulator.all-tests
  (:require [clojure.test :refer [deftest is successful? run-tests]]
            [com.github.bjuvensjo.rsimulator.core.xml]))

(def namespaces-to-test (->> (all-ns)
                             (map str)
                             (filter (fn [x] (re-matches #"com.github.bjuvensjo.rsimulator\..*" x)))
                             (remove (fn [x] (= "com.github.bjuvensjo.rsimulator.all-tests" x)))
                             (map symbol)))

(defn execute-tests []
  (time (apply run-tests namespaces-to-test)))

