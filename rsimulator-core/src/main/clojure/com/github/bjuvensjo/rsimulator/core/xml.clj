(ns com.github.bjuvensjo.rsimulator.core.xml
  (:require [clojure.data.xml :as xml]
            [clojure.test :refer [is deftest]]))


(defn match-tag?
  "Tags are namespace aware. Both the namespace and the name must match. Returns true or an error string describing the error."
  {:test (fn []
           ; Match as strings
           (is (= (match-tag? :ns-part/name-part :ns-part/name-part)
                  true))
           ; Namespace match as regex
           (is (= (match-tag? :ns-part/name-part :n.*t/name-part)
                  true))
           ; Mismatch in name
           (is (= (match-tag? :ns-part/name-part :ns-part/name-other)
                  "Tag name mismatch | actual: name-part | mock: name-other"))
           ; Mock has no namespace given
           (is (= (match-tag? :ns-part/name-part :name-part)
                  "Tag namespace mismatch as strings and as pattern | actual: ns-part | mock: "))
           ; Actual has no namespace given
           (is (= (match-tag? :name-part :ns-part/name-part)
                  "Tag namespace mismatch as strings and as pattern | actual:  | mock: ns-part"))
           ; Mismatch in namespace
           (is (= (match-tag? :ns-part/name-part :ns-other/name-part)
                  "Tag namespace mismatch as strings and as pattern | actual: ns-part | mock: ns-other")))}
  [actual-tag mock-tag]
  (cond (not= (name actual-tag) (name mock-tag))
        (str "Tag name mismatch | actual: " (name actual-tag) " | mock: " (name mock-tag))

        (let [namespace-of-actual-tag (namespace actual-tag)
              namespace-of-mock-tag (namespace mock-tag)]
          (and (not= namespace-of-actual-tag namespace-of-mock-tag)
               (try (let [mock-tag-pattern (re-pattern namespace-of-mock-tag)]
                      (not (re-matches mock-tag-pattern namespace-of-actual-tag)))
                    (catch Exception _
                      true))))
        (str "Tag namespace mismatch as strings and as pattern | actual: " (namespace actual-tag) " | mock: " (namespace mock-tag))

        :else
        true))


(defn match-attributes?
  "Returns true or an error string describing the error."
  {:test (fn []
           ; Match attributes as strings and as regex
           (is (= (match-attributes? {:attr-a "a" :attr-b "closure"}
                                     {:attr-a "a" :attr-b "clo.*e"})
                  true))
           ; Incorrect number of attributes
           (is (= (match-attributes? {:attr-a "a" :attr-b "closure" :attr-c "c"}
                                     {:attr-a "a" :attr-b "clo.*e"})
                  "Number of attributes mismatch | actual: 3 | mock: 2"))
           ; Attribute value mismatch
           (is (= (match-attributes? {:attr-a "a"}
                                     {:attr-a "b"})
                  "Attribute value mismatch as string and as pattern | key: attr-a | actual: a | mock: b"))
           ; Missing key in actual attributes
           (is (= (match-attributes? {:attr-c "c"}
                                     {:attr-a "b"})
                  "Attribute key missing | mocked key: attr-a")))}
  [actual-attributes mock-attributes]
  (let [number-of-attributes-actual (count (keys actual-attributes))
        number-of-attributes-mock (count (keys mock-attributes))]
    (if (not= number-of-attributes-mock number-of-attributes-actual)
      (str "Number of attributes mismatch | actual: " number-of-attributes-actual " | mock: " number-of-attributes-mock)
      (or (->> (seq mock-attributes)
               (some (fn [[mock-key mock-value]]
                       (let [actual-value (get actual-attributes mock-key)]
                         (cond (not actual-value)
                               (str "Attribute key missing | mocked key: " (name mock-key))

                               (and (not= actual-value mock-value)
                                    (let [mock-value-pattern (re-pattern mock-value)]
                                      (not (re-matches mock-value-pattern actual-value))))
                               (str "Attribute value mismatch as string and as pattern | key: " (name mock-key) " | actual: " actual-value " | mock: " mock-value)

                               :else
                               nil)))))
          true))))

(declare matches-clj-xml?)

(defn match-content?
  "Returns a vector of matching groups or an error message."
  {:test (fn []
           ; Should be able to match with .*
           (is (= (match-content? [{:tag     :bar
                                    :attrs   {:abc "123"}
                                    :content [{:tag     :baz
                                               :content ["baz"]}]}]
                                  [".*"])
                  []))
           ; Should match single value
           (is (= (match-content? ["foo-value"]
                                  ["foo-value"])
                  []))
           ; Incorrect number of children
           (is (= (match-content? [{:tag     :bar
                                    :content ["bar value"]}]
                                  [{:tag     :bar
                                    :content ["bar value"]}
                                   {:tag     :baz
                                    :content ["The (.*)"]}])
                  "Incorrect number of children | mock: 2 | actual: 1"))
           ; Should get groups in returned vector
           (is (= (match-content? [{:tag     :bar
                                    :content ["bar value"]}
                                   {:tag     :baz
                                    :content ["The baz value"]}]
                                  [{:tag     :bar
                                    :content ["b(.*)r value"]}
                                   {:tag     :baz
                                    :content ["The (.*)"]}])
                  ["a" "baz value"]))
           ; Inner error keys should be displayed in message
           (is (= (match-content? [{:tag     :bar
                                    :content [{:tag     :baz
                                               :content ["baz"]}]}]
                                  [{:tag     :bar
                                    :content [{:tag     :baz
                                               :content ["BAZ"]}]}])
                  "bar | baz | Values mismatch | actual: baz | mock: BAZ")))}
  [actual-content mock-content]
  (cond (= mock-content [".*"])
        []

        (not= (count actual-content) (count mock-content))
        (str "Incorrect number of children | mock: " (count mock-content) " | actual: " (count actual-content))

        :else
        (loop [[a & ar] actual-content
               [m & mr] mock-content
               groups []]
          (cond (nil? a)
                groups

                (and (string? a) (string? m))
                (if (= a m)
                  []
                  (let [pattern (re-pattern m)
                        groups (re-matches pattern a)]
                    (if groups
                      (drop 1 groups)
                      (str "Values mismatch | actual: " a " | mock: " m))))

                :else
                (let [result (matches-clj-xml? a m)]
                  (if (string? result)
                    (str (when-let [tag (:tag a)] (str (name tag) " | ")) result)
                    (recur ar mr (concat groups result))))))))


(defn matches-clj-xml?
  {:test (fn []
           (is (= (matches-clj-xml? {:tag :ns-part/name-part}
                                    {:tag :ns-other/name-part})
                  "Tag namespace mismatch as strings and as pattern | actual: ns-part | mock: ns-other"))
           (is (= (matches-clj-xml? {:tag :ns-part/name-part}
                                    {:tag :ns-other/name-part})
                  "Tag namespace mismatch as strings and as pattern | actual: ns-part | mock: ns-other"))
           (is (= (matches-clj-xml? {:tag :foo}
                                    {:tag :bar})
                  "Tag name mismatch | actual: foo | mock: bar"))
           (is (= (matches-clj-xml? {:tag     :ns/foo
                                     :content ["foo-value"]}
                                    {:tag     :ns/foo
                                     :content ["foo-value"]})
                  []))
           (is (= (matches-clj-xml? {:tag   :a
                                     :attrs {:attr "a11"}}
                                    {:tag   :a
                                     :attrs {:attr "a55"}}))
               "Attribute value mismatch as string and as pattern | key: attr | actual: a11 | mock: a55")
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
  (let [{actual-tag :tag actual-attrs :attrs actual-content :content} actual
        {mock-tag :tag mock-attrs :attrs mock-content :content} mock]
    (let [tag-match (match-tag? actual-tag mock-tag)]
      (if (string? tag-match)
        tag-match
        (let [attributes-match (match-attributes? actual-attrs mock-attrs)]
          (if (string? attributes-match)
            attributes-match
            (match-content? actual-content mock-content)))))))


(defn matches-xml-as-strings?
  {:test (fn []
           (is (= (matches-xml-as-strings?
                    (str "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "    <soap:Body>"
                         "        <GetDocumentById xmlns=\"http://LF.Dokumenttjansten.Service\">"
                         "            <Request abc=\"123\">"
                         "                <RequestContext>"
                         "                    <Environment>LOCAL</Environment>"
                         "                    <SecurityToken>a-token-123</SecurityToken>"
                         "                    <SubSysCode></SubSysCode>"
                         "                    <SysCode>CSP</SysCode>"
                         "                    <TransID>trans-id-123-abc</TransID>"
                         "                </RequestContext>"
                         "                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>"
                         "                <ReturnType>rtByteStream</ReturnType>"
                         "            </Request>"
                         "        </GetDocumentById>"
                         "    </soap:Body>"
                         "</soap:Envelope>")
                    (str "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "    <soap:Body>"
                         "        <GetDocumentById xmlns=\".*\">"
                         "            <Request abc=\"12\">"
                         "                <RequestContext>"
                         "                    <Environment>LOCAL</Environment>"
                         "                    <SecurityToken>.*</SecurityToken>"
                         "                    <SubSysCode></SubSysCode>"
                         "                    <SysCode>CSP</SysCode>"
                         "                    <TransID>.*</TransID>"
                         "                </RequestContext>"
                         "                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>"
                         "                <ReturnType>rtByteStream</ReturnType>"
                         "            </Request>"
                         "        </GetDocumentById>"
                         "    </soap:Body>"
                         "</soap:Envelope>"))
                  "Body | GetDocumentById | Request | Attribute value mismatch as string and as pattern | key: abc | actual: 123 | mock: 12"))
           (is (= (matches-xml-as-strings?
                    (str "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "    <soap:Body>"
                         "        <GetDocumentById xmlns=\"http://LF.Dokumenttjansten.Service\">"
                         "            <Request abc=\"123\">"
                         "                <RequestContext>"
                         "                    <Environment>LOCAL</Environment>"
                         "                    <SecurityToken>a-token-123</SecurityToken>"
                         "                    <SubSysCode></SubSysCode>"
                         "                    <SysCode>CSP</SysCode>"
                         "                    <TransID>trans-id-123-abc</TransID>"
                         "                </RequestContext>"
                         "                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>"
                         "                <ReturnType>rtByteStream</ReturnType>"
                         "            </Request>"
                         "        </GetDocumentById>"
                         "    </soap:Body>"
                         "</soap:Envelope>")
                    (str "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "    <soap:Body>"
                         "        <GetDocumentById xmlns=\"http://LF..*.Service\">"
                         "            <Request abc=\"123\">"
                         "                <RequestContext>"
                         "                    <Environment>LOCAL</Environment>"
                         "                    <SecurityToken>(.*)</SecurityToken>"
                         "                    <SubSysCode></SubSysCode>"
                         "                    <SysCode>CSP</SysCode>"
                         "                    <TransID>(.*)</TransID>"
                         "                </RequestContext>"
                         "                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>"
                         "                <ReturnType>rtByteStream</ReturnType>"
                         "            </Request>"
                         "        </GetDocumentById>"
                         "    </soap:Body>"
                         "</soap:Envelope>"))
                  ["a-token-123" "trans-id-123-abc"]))
           (is (= (matches-xml-as-strings?
                    (str "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "    <soap:Body>"
                         "        <GetDocumentById xmlns=\"http://LF.Dokumenttjansten.Service\">"
                         "            <Request abc=\"123\">"
                         "                <RequestContext>"
                         "                    <Environment>LOCAL</Environment>"
                         "                    <SecurityToken>a-token-123</SecurityToken>"
                         "                    <SubSysCode></SubSysCode>"
                         "                    <SysCode>CSP</SysCode>"
                         "                    <TransID>trans-id-123-abc</TransID>"
                         "                </RequestContext>"
                         "                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>"
                         "                <ReturnType>rtByteStream</ReturnType>"
                         "            </Request>"
                         "        </GetDocumentById>"
                         "    </soap:Body>"
                         "</soap:Envelope>")
                    (str "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "    <soap:Body>"
                         "        <GetDocumentById xmlns=\".*\">"
                         "            <Request abc=\"123\">"
                         "                <RequestContext>"
                         "                    <Environment>LOCAL</Environment>"
                         "                    <SecurityToken>(.*)</SecurityToken>"
                         "                    <SubSysCode></SubSysCode>"
                         "                    <SysCode>CSP</SysCode>"
                         "                    <TransID>(.*)</TransID>"
                         "                </RequestContext>"
                         "                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>"
                         "                <ReturnType>rtByteStream</ReturnType>"
                         "            </Request>"
                         "        </GetDocumentById>"
                         "    </soap:Body>"
                         "</soap:Envelope>"))
                  ["a-token-123" "trans-id-123-abc"])))}
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


(comment
  (let [input-xml (java.io.StringReader. "<x xmlns=\"x\" targetNamespace=\"y\" xmlns:z=\"z\"><z:a/></x>")]
    (xml/parse input-xml :skip-whitespace true))

  #xml/element{:tag     :xmlns.x/x,
               :attrs   {:targetNamespace "y"},
               :content [#xml/element{:tag :xmlns.z/a}]}

  (let [input-xml (java.io.StringReader. "<x xmlns=\".*\"><a/></x>")]
    (xml/parse input-xml :skip-whitespace true))

  (let [input-xml (java.io.StringReader. "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n    <soap:Body>\n        <GetDocumentById xmlns=\"http://LF.Dokumenttjansten.Service\">\n            <Request>\n                <RequestContext>\n                    <Environment>LOCAL</Environment>\n                    <SecurityToken>.*</SecurityToken>\n                    <SubSysCode></SubSysCode>\n                    <SysCode>CSP</SysCode>\n                    <TransID>.*</TransID>\n                </RequestContext>\n                <DokumentId>37d510dd-9194-40c6-9e0b-238b10e2e80d</DokumentId>\n                <ReturnType>rtByteStream</ReturnType>\n            </Request>\n        </GetDocumentById>\n    </soap:Body>\n</soap:Envelope>")]
    (xml/parse input-xml :skip-whitespace true))
  )