package com.github.bjuvensjo.rsimulator.core.handler.regexp

import groovy.namespace.QName
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import groovy.xml.XmlNodePrinter
import groovy.xml.XmlParser
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

import java.util.regex.Matcher

// Element order is significant.
// Attribute order is not significant.
// Supports regexp for element and attribute values and element structures.
// Avoid using regexp group for attribute values since they are unordered.
@Slf4j
@CompileStatic
class XmlMatcher {
    @ToString
    static class Result {
        String errorNode
        String errorOtherNode
        String errorMessage
        List<String> groups = []
    }

    Result match(String xml, String otherXml, boolean validating = false, boolean namespaceAware = false) {
        try {
            Node xmlRoot = parse(xml, validating, namespaceAware)
            // If otherXml is only a regexp wildcard, try to match
            if ('.*' == otherXml.trim() || '(.*)' == otherXml.trim()) {
                Result result = matchRegex(asString(xmlRoot, namespaceAware), otherXml.trim())
                if (!result.errorMessage) {
                    log.info('Result: {}', result)
                    return result
                }
            }

            Result result = matchNode(
                xmlRoot,
                parse(otherXml, validating, namespaceAware),
                namespaceAware
            )
            log.info('Result: {}', result)
            return result
        } catch (Exception e) {
            Result result = new Result(errorMessage: "Cannot match ${xml} and ${otherXml}: ${e.getMessage()}")
            log.error('Match error. Result: {}', result, e)
            return result
        }
    }

    protected Result matchNode(Node node, Node otherNode, boolean namespaceAware) {
        // This would be enough if not for detailed error message and different namespace prefixes when namespace aware
        Result regexResult = matchRegex(node, otherNode, namespaceAware)
        if (!regexResult.errorMessage) {
            return regexResult
        }

        // We come here if the nodes as wholes not matches as regex
        // The main reason is to provide more detailed error message
        List<String> groups = []
        for (Closure f : [this.&matchName, this.&matchAttributes, this.&matchContent]) {
            Result result = f.call(node, otherNode, namespaceAware) as Result
            if (result.errorMessage) {
                return result
            } else {
                groups.addAll(result.groups)
            }
        }
        new Result(groups: groups)
    }

    protected Result matchRegex(Node node, Node otherNode, boolean namespaceAware) {
        Result result = matchRegex(asString(node, namespaceAware), asString(otherNode, namespaceAware))
        if (result.errorMessage) {
            return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'Regex not matching.')
        }
        result
    }

    protected Result matchRegex(String s, String otherS) {
        Matcher m = s =~ otherS
        if (m.matches()) {
            List<String> groups = getGroups(m)
            return new Result(groups: groups)
        }
        new Result(errorMessage: 'Regex not matching.')
    }

    protected Result matchName(Node node, Node otherNode, boolean namespaceAware) {
        if (node.name().class != otherNode.name().class) {
            return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'One node name namespaced and one not')
        }
        if (node.name() instanceof String || otherNode.name() instanceof String) {
            if (node.name() == otherNode.name()) {
                return new Result()
            } else {
                return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'Names not matching')
            }
        }
        // If namespaceAware and namespaces element
        QName qName = node.name() as QName
        QName otherQName = otherNode.name() as QName
        if (qName.localPart == otherQName.localPart && qName.namespaceURI == otherQName.namespaceURI) {
            return new Result()
        }
        new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'Names not matching')
    }

    protected Result matchAttributes(Node node, Node otherNode, boolean namespaceAware) {
        List<String> groups = []
        Map attributes = node.attributes()
        Map otherAttributes = otherNode.attributes()
        if (attributes.size() != otherAttributes.size()) {
            return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'Different number of attributes')
        }
        for (def a : attributes) {
            Object value = otherAttributes.get(a.key)
            if (!value) {
                return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'Missing attribute ' + a.key)
            }
            Matcher m = a.value =~ value
            if (m.matches()) {
                groups.addAll(getGroups(m))
            } else {
                return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: "Attribute values not matching for ${a.key}: '${a.value}' != '${value}'".toString())
            }
        }
        new Result(groups: groups)
    }

    protected Result matchContent(Node node, Node otherNode, boolean namespaceAware) {
        List children = node.children()
        List otherChildren = otherNode.children()
        if (children.size() != otherChildren.size()) {
            return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: 'Different number of children')
        }
        List<String> groups = []
        Map<String, List<String>> groupsMap = [:]
        for (int i = 0; i < children.size(); i++) {
            Object child = children[i]
            Object otherChild = otherChildren[i]
            if (child instanceof String) {
                Matcher m = child.toString().trim() =~ otherChild.toString().trim()
                if (m.matches()) {
                    List<String> childGroups = getGroups(m)
                    groups.addAll(childGroups)
                    groupsMap[getPath(node)] = childGroups
                } else {
                    return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: "Values not matching: '${child}' != '${otherChild}'".toString())
                }
            } else {
                if (child instanceof Node && otherChild instanceof Node) {
                    Result childResult = matchNode(child as Node, otherChild as Node, namespaceAware)
                    if (childResult.errorMessage) {
                        return childResult
                    }
                    groups.addAll(childResult.groups)
                } else {
                    return new Result(errorNode: getPath(node), errorOtherNode: getPath(otherNode), errorMessage: "Both children are not nodes: '${child}' != '${otherChild}'".toString())
                }
            }
        }
        new Result(groups: groups)
    }

    private static String asString(Node node, boolean useNamespaces) {
        StringWriter sw = new StringWriter()
        new XmlNodePrinter(new IndentPrinter(sw, '', false, false)).with {
            namespaceAware = useNamespaces
            expandEmptyElements = false
            preserveWhitespace = false
            print(node)
        }
        sw.toString()
    }

    private static List<String> getGroups(Matcher matcher) {
        List<String> groups = []
        if (matcher.groupCount()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                groups << matcher.group(i)
            }
        }
        groups
    }

    private static String getPath(Node node) {
        Node parent = node.parent()
        if (!parent) {
            return node.name()
        }
        List parents = []
        while (parent) {
            parents << parent.name()
            parent = parent.parent()
        }
        parents.reverse().join('.') + '.' + node.name()
    }

    private static Node parse(String xml, boolean validating, boolean namespaceAware) {
        XmlParser xmlParser = new XmlParser(validating, namespaceAware)
        xmlParser.setKeepIgnorableWhitespace(false)
        xmlParser.setTrimWhitespace(true)
        if (validating) {
            xmlParser.setErrorHandler(new ErrorHandler() {
                @Override
                void warning(SAXParseException e) throws SAXException {
                    log.debug('Parse warning: {}, {}', e.getMessage(), xml)
                }

                @Override
                void error(SAXParseException e) throws SAXException {
                    log.debug('Parse error: {}, {}', e.getMessage(), xml)
                }

                @Override
                void fatalError(SAXParseException e) throws SAXException {
                    log.error('Parse fatalError: {}, {}', e.getMessage(), xml)
                    throw e
                }
            })
        }
        xmlParser.parseText(xml)
    }
}
