(ns com.github.bjuvensjo.rsimulator.core.xml
  (:require [clojure.data.xml :as xml]
            [clojure.test :refer [is]]))

(defn matches-clj-xml?
  "Matches clojure representation of xml."
  {:test (fn []
           ; Should fail on different tags
           (is (= (matches-clj-xml? {:tag :foo}
                                    {:tag :bar})
                  "Tag mismatch | actual: foo | mock: bar"))
           ; Should match when correct string content
           (is (= (matches-clj-xml? {:tag     :foo
                                     :content ["foo-value"]}
                                    {:tag     :foo
                                     :content ["foo-value"]})
                  []))
           ; Should match when correct regex content
           (is (= (matches-clj-xml? {:tag     :foo
                                     :content ["foo-value"]}
                                    {:tag     :foo
                                     :content ["foo-.*"]})
                  []))
           ; Should match inner list structure
           (is (= (matches-clj-xml? {:tag     :foo
                                     :content [{:tag     :bar
                                                :content ["bar value"]}
                                               {:tag     :baz
                                                :content ["The baz value"]}]}
                                    {:tag     :foo
                                     :content [{:tag     :bar
                                                :content ["bar value"]}
                                               {:tag     :baz
                                                :content ["The (.*)"]}]})
                  ["baz value"]))
           ; Big match with .*
           (is (= (matches-clj-xml? {:tag     :foo
                                     :content [{:tag     :bar
                                                :content ["bar value"]}
                                               {:tag     :baz
                                                :content ["The baz value"]}]}
                                    {:tag     :foo
                                     :content [".*"]})
                  []))
           ; Should give accurate error message with keys to error
           (is (= (matches-clj-xml? {:tag     :foo
                                     :content [{:tag     :bar
                                                :content [{:tag     :baz
                                                           :content ["baz"]}]}]}
                                    {:tag     :foo
                                     :content [{:tag     :bar
                                                :content [{:tag     :baz
                                                           :content ["BAZ"]}]}]})
                  "bar | baz | Values mismatch | actual: baz | mock: BAZ")))}
  [actual mock]
  (let [{actual-tag :tag actual-content :content} actual
        {mock-tag :tag mock-content :content} mock]
    (cond (and (string? actual) (string? mock))
          (if (= actual mock)
            []
            (let [pattern (re-pattern mock)
                  groups (re-matches pattern actual)]
              (cond (coll? groups)
                    (drop 1 groups)

                    groups
                    []

                    :else
                    (str "Values mismatch | actual: " actual " | mock: " mock))))

          (not= actual-tag mock-tag)
          (str "Tag mismatch | actual: " (name actual-tag) " | mock: " (name mock-tag))

          (= mock-content [".*"])
          []

          (not= (count actual-content) (count mock-content))
          (str "Incorrect number of children | mock: " (count mock-content) " | actual: " (count actual-content))

          :else
          (loop [[a & ar] actual-content
                 [m & mr] mock-content
                 groups []]
            (cond (nil? a)
                  groups

                  :else
                  (let [result (matches-clj-xml? a m)]
                    (if (string? result)

                      (str (when-let [tag (:tag a)]
                             (str (name tag) " | "))
                           result)
                      (recur ar mr (concat groups result)))))))))


(defn matches-xml-as-strings?
  [actual mock]
  (let [actual-as-clj-xml (xml/parse (java.io.StringReader. actual) :skip-whitespace true)
        mock-as-clj-xml (xml/parse (java.io.StringReader. mock) :skip-whitespace true)]
    (matches-clj-xml? actual-as-clj-xml mock-as-clj-xml)))


(defn create-response
  {:test (fn []
           (is (= (create-response "<response><tag1>${1}</tag1><tag2>${2}</tag2><tag3>${1}</tag3>"
                                   ["Value1" "Value2"])
                  "<response><tag1>Value1</tag1><tag2>Value2</tag2><tag3>Value1</tag3>")))}
  [mock-response groups]
  (->> groups
       (map-indexed (fn [index replacement] [(inc index) replacement]))
       (reduce (fn [response [index replacement]]
                 (clojure.string/replace response (str "${" index "}") replacement))
               mock-response)))
