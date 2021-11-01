package com.github.bjuvensjo.rsimulator.core.handler.parser.xml;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.List;

public class Matcher {
    private static final IFn matchesXMLAsStrings;
    private static final IFn cljCreateResponse;

    static {
        IFn require = Clojure.var("clojure.core", "require");
        String ns = "com.github.bjuvensjo.rsimulator.core.xml";
        require.invoke(Clojure.read(ns));
        matchesXMLAsStrings = Clojure.var(ns, "matches-xml-as-strings?");
        cljCreateResponse = Clojure.var(ns, "create-response");
    }

    public static MatcherResponse match(String actual, String mock) {
        MatcherResponse xmlResponse = new MatcherResponse();
        Object cljResponse = matchesXMLAsStrings.invoke(actual, mock);
        if (cljResponse instanceof String) {
            xmlResponse.errorMessage = (String) cljResponse;
        } else {
            xmlResponse.groups = (List<String>) cljResponse;
        }
        return xmlResponse;
    }

    public static String createResponse(String mockResponse, List<String> groups) {
        return (String) cljCreateResponse.invoke(mockResponse, groups);
    }
}
