package com.github.bjuvensjo.rsimulator.core.handler.regexp

import spock.lang.Specification
import spock.lang.Unroll

class XmlMatcherTest extends Specification {
    XmlMatcher spec = new XmlMatcher()

    @Unroll
    def "Match success not namespaceAware"(String xml, String otherXml, List<String> groups) {
        when:
        XmlMatcher.Result result = spec.match(xml, otherXml)
        then:
        !result.errorNode
        !result.errorOtherNode
        !result.errorMessage
        result.groups == groups
        where:
        xml                                                     | otherXml                                                                   | groups
        // With and without prolog
        '<?xml version="1.0" encoding="UTF-8"?><x/>'            | '<x/>'                                                                     | []

        // Insignificant whitespaces
        '<?xml version="1.0" encoding="UTF-8"?><x><y>y</y></x>' | '<x>\n<y>    y  \n</y></x>\n'                                              | []

        // Element name. Attribute name, value and order. Regexp of element and attribute values.
        '<x x1="x1" x2="x2"><y y="y">y</y><z z="z">z</z></x>'   | '<x x1=".*" x2="([\\w]+)"><y y="([a-z0-9]{1})">y</y><z z="z">(.*)</z></x>' | ['x2', 'y', 'z']

        // Regexp of element structure
        '<x><y><z>z</z></y></x>'                                | '<x>(.*)</x>'                                                              | ['<y><z>z</z></y>']
        '<x>\n<y><z>z</z></y></x>'                              | '(.*)'                                                                     | ['<x><y><z>z</z></y></x>']
    }

    @Unroll
    def "Match success namespaceAware"(String xml, String otherXml, List<String> groups) {
        when:
        XmlMatcher.Result result = spec.match(xml, otherXml, true, true)
        then:
        !result.errorNode
        !result.errorOtherNode
        !result.errorMessage
        result.groups == groups
        where:
        xml                                                       | otherXml                                                                  | groups
        // Different namespace order and prefixes
        '<x xmlns="x" targetNamespace="y" xmlns:z="z"><z:a/></x>' | '<x xmlns:a="z" targetNamespace="y" xmlns="x"><a:a/></x>'                 | []

        // Regexp namespaces and different order and prefixes
        '<x xmlns="x" targetNamespace="y" xmlns:z="z"><z:a/></x>' | '<x xmlns:a=".*" targetNamespace="[\\w]+" xmlns="[a-z0-9]{1}"><a:a/></x>' | []
    }

    @Unroll
    def "Match parse error"(String xml, String otherXml, String errorNode, String errorOtherNode, String errorMessage) {
        when:
        XmlMatcher.Result result = spec.match(xml, otherXml, true, true)
        then:
        result.errorNode == errorNode
        result.errorOtherNode == errorOtherNode
        result.errorMessage == errorMessage
        !result.groups
        where:
        xml    | otherXml | errorNode | errorOtherNode | errorMessage
        // Element name
        '<x>'  | '<x/>'   | null      | null           | 'Cannot match <x> and <x/>: XML document structures must start and end within the same entity.'
        '<x/>' | '<x>'    | null      | null           | 'Cannot match <x/> and <x>: XML document structures must start and end within the same entity.'
    }

    @Unroll
    def "Match failure not namespaceAware"(String xml, String otherXml, String errorNode, String errorOtherNode, String errorMessage) {
        when:
        XmlMatcher.Result result = spec.match(xml, otherXml)
        then:
        result.errorNode == errorNode
        result.errorOtherNode == errorOtherNode
        result.errorMessage == errorMessage
        !result.groups
        where:
        xml                       | otherXml                  | errorNode | errorOtherNode | errorMessage
        // Element name
        '<x/>'                    | '<a/>'                    | 'x'       | 'a'            | 'Names not matching'
        '<x><y></y></x>'          | '<x><b></b></x>'          | 'x.y'     | 'x.b'          | 'Names not matching'
        '<x><y></y><z></z></x>'   | '<x><b></b><z></z></x>'   | 'x.y'     | 'x.b'          | 'Names not matching'
        '<x><y></y><z></z></x>'   | '<x><y></y><c></c></x>'   | 'x.z'     | 'x.c'          | 'Names not matching'

        // Element order
        '<x><y></y><z></z></x>'   | '<x><z></z><y></y></x>'   | 'x.y'     | 'x.z'          | 'Names not matching'

        // Element value
        '<x>x</x>'                | '<x>a</x>'                | 'x'       | 'x'            | "Values not matching: 'x' != 'a'"
        '<x><y>y</y><z></z></x>'  | '<x><y>a</y><z></z></x>'  | 'x.y'     | 'x.y'          | "Values not matching: 'y' != 'a'"
        '<x><y></y><z>z</z></x>'  | '<x><y></y><z>a</z></x>'  | 'x.z'     | 'x.z'          | "Values not matching: 'z' != 'a'"
        '<x><y>y</y><z>z</z></x>' | '<x><y>a</y><z>b</z></x>' | 'x.y'     | 'x.y'          | "Values not matching: 'y' != 'a'"

        // Attribute name and order
        '<x y="1"/>'              | '<x a="1"/>'              | 'x'       | 'x'            | 'Missing attribute y'
        '<x y="1" z="1"/>'        | '<x a="1" z="1"/>'        | 'x'       | 'x'            | 'Missing attribute y'
        '<x y="1" z="1"/>'        | '<x y="1" a="1"/>'        | 'x'       | 'x'            | 'Missing attribute z'

        // Attribute value
        '<x y="1"/>'              | '<x y="2"/>'              | 'x'       | 'x'            | "Attribute values not matching for y: '1' != '2'"

        // Different number of children
        '<x>x</x>'                | '<x></x>'                 | 'x'       | 'x'            | 'Different number of children'

        // Different number of attributes
        '<x y="1"/>'              | '<x/>'                    | 'x'       | 'x'            | 'Different number of attributes'
    }

    @Unroll
    def "Match failure namespaceAware"(String xml, String otherXml, String errorNode, String errorOtherNode, String errorMessage) {
        when:
        XmlMatcher.Result result = spec.match(xml, otherXml, true, true)
        then:
        result.errorNode == errorNode
        result.errorOtherNode == errorOtherNode
        result.errorMessage == errorMessage
        !result.groups
        where:
        xml                                   | otherXml                              | errorNode   | errorOtherNode | errorMessage
        // Element name
        '<x xmlns="x"></x>'                   | '<x xmlns="a"> </x>'                  | '{x}x'      | '{a}x'         | 'Names not matching'
        '<x xmlns="x" xmlns:n="y"><n:y/></x>' | '<x xmlns="x" xmlns:n="a"><n:y/></x>' | '{x}x.{y}y' | '{x}x.{a}y'    | 'Names not matching'
    }

    def "SOAP example"() {
        when:
        String xml = ''' <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" xmlns:h="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <s:Header/>
               <s:Body>
                  <h:SayHelloRequest>
                     <from a="a" b="b">Test3</from>
                     <to>Simulator</to>
                     <greeting>hey</greeting>
                     <foo>
                       <bar>
                         <baz>baz</baz>
                       </bar>                           
                     </foo>
                  </h:SayHelloRequest>
               </s:Body>
            </s:Envelope> '''
        String otherXml = '''<?xml version="1.0" encoding="UTF-8"?>
            <soapenv:Envelope xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
               <soapenv:Header></soapenv:Header>
               <soapenv:Body>
                  <hel:SayHelloRequest>
                     <from b="b" a="([a-z]+)">(.*)</from>
                     <to>([\\w]{5}[ab]tor)</to>
                     <greeting>[^z]*</greeting>
                     <foo>(.*)</foo>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>'''
        XmlMatcher.Result result = spec.match(xml, otherXml, true, true)
        then:
        !result.errorNode
        !result.errorOtherNode
        !result.errorMessage
        result.groups == ['a', 'Test3', 'Simulator', '<bar><baz>baz</baz></bar>']
    }
}
