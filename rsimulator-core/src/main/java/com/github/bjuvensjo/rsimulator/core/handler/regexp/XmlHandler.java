package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.google.inject.Singleton;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * XmlHandler is a regular expression handler for xml (.xml).
 *
 * @see AbstractHandler
 */
@Singleton
public class XmlHandler extends AbstractHandler {
    private final Logger log = LoggerFactory.getLogger(XmlHandler.class);
    private final OutputFormat format;

    public XmlHandler() {
        this.format = OutputFormat.createCompactFormat();
        format.setSuppressDeclaration(true); // To not have the ? in the declaration interpreted as regular expressions.
        format.setExpandEmptyElements(true); // Required for .* to generate match on empty element, e.g. <tag>.*</tag> == <tag></tag>
    }

    @Override
    protected String getExtension() {
        return "xml";
    }

    @Override
    protected String format(String request) {
        return Optional.ofNullable(request)
                .filter(r -> !r.isBlank())
                .map(r -> {
                    try {
                        return DocumentHelper.parseText(r);
                    } catch (DocumentException e) {
                        log.error("Can not parse {}", r, e);
                        return null;
                    }
                })
                .map(document -> props.ignoreXmlNamespaces() ? toIgnoredNamespacesString(document.getRootElement()) : toNamespacesString(document))
                .orElse(request);
    }

    @Override
    protected String escape(String request, boolean isCandidate) {
        return request;
    }

    private String toNamespacesString(Document document) {
        try (StringWriter sw = new StringWriter()) {
            XMLWriter xmlWriter = new XMLWriter(sw, format);
            xmlWriter.write(document);
            xmlWriter.close();
            return sw.toString().trim();
        } catch (IOException e) {
            log.error("Can not write {}", document, e);
            return null;
        }
    }

    private String toIgnoredNamespacesString(Element element) {
        return IntStream.range(0, element.nodeCount())
                .mapToObj(element::node)
                .map(node -> {
                    // These conditions ignores namespace nodes
                    if (node instanceof Element) {
                        return toIgnoredNamespacesString((Element) node);
                    }
                    if (node instanceof Text || node instanceof CDATA) {
                        return node.getText().trim();
                    }
                    return "";
                })
                .collect(Collectors.joining(
                        "",
                        String.format("<%s>", element.attributes().stream()
                                .map(Node::asXML)
                                .collect(Collectors.joining(" ", element.getName() + " ", "")).trim()),
                        String.format("</%s>", element.getName())));
    }
}
