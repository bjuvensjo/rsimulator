package com.github.bjuvensjo.rsimulator.core.clojure;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import org.junit.Assert;

import java.util.Map;

public class Test {

    @org.junit.Test
    public void clojureInlineTests() {
        IFn require = Clojure.var("clojure.core", "require");
        String ns = "com.github.bjuvensjo.rsimulator.all-tests";
        String fnName = "execute-tests";
        require.invoke(Clojure.read(ns));
        IFn executeTestsFn = Clojure.var(ns, fnName);

        Map result = (PersistentArrayMap) executeTestsFn.invoke();

        Assert.assertEquals(0, (long) result.get(Keyword.intern("error")));
        Assert.assertEquals(0, (long) result.get(Keyword.intern("fail")));
        Assert.assertTrue((long) result.get(Keyword.intern("pass")) > 0);
        Assert.assertTrue((long) result.get(Keyword.intern("test")) > 0);

    }
}
