package com.github.bjuvensjo.rsimulator.core

import java.nio.file.Paths

class TestUtil {

    static String getRootPath(String resource) {
        try {
            Paths.get(TestUtil.class.getResource(resource).toURI()).toString()
        } catch (URISyntaxException e) {
            throw new RuntimeException(e)
        }
    }
}
