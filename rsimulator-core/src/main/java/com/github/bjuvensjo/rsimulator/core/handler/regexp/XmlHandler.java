package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.google.inject.Singleton;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * XmlHandler is a regular expression handler for xml (.xml).
 *
 * @author Magnus Bjuvensjö
 * @see AbstractHandler
 */
@Singleton
public class XmlHandler extends AbstractHandler {
    private static final String EXTENSION = "xml";
    private Logger log = LoggerFactory.getLogger(XmlHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExtension() {
        return EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String format(String request) {
        String result = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(request.getBytes());
            Document doc = new SAXBuilder().build(bis);
            bis.close();
            Format format = Format.getCompactFormat();
            // Required for .* to generate match on empty element
            // <tag>.*</tag> == <tag></tag>
            format.setExpandEmptyElements(true);
            // To not have the ? in the declaration interpreted as regular expressions.
            format.setOmitDeclaration(true);
            XMLOutputter out = new XMLOutputter(format);
            out.output(doc, bos);
            result = new String(bos.toByteArray());
            bos.close();
        } catch (Exception e) {
            log.error(null, e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String escape(String request, boolean isCandidate) {
        return request;
    }
}
