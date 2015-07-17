package com.github.bjuvensjo.rsimulator.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FileUtilsTest {
    private FileUtils fileUtils;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        fileUtils = injector.getInstance(FileUtils.class);
    }

    @Test
    public void testFindRequests() {
        List<Path> xmlRequests = fileUtils.findRequests(new File(getClass().getResource("/").getPath()).toPath(), "xml");
        assertTrue("xmlRequests", xmlRequests.size() > 0);
        for (Path path : xmlRequests) {
            assertTrue("xmlFile", path.toString().endsWith("Request.xml"));
        }
        List<Path> txtRequests = fileUtils.findRequests(new File(getClass().getResource("/").getPath()).toPath(), "txt");
        assertTrue("txtRequests", txtRequests.size() > 0);
        for (Path path : txtRequests) {
            assertTrue("txtFile",  path.toString().endsWith("Request.txt"));
        }
    }

    @Test
    public void testRead() {
        String resource = "/test1/Test.properties";
        try {
            File file = new File(getClass().getResource(resource).getPath());
            String content = fileUtils.read(file.toPath());
            String cachedContent = fileUtils.read(file.toPath());
            assertNotNull("classFileContent", content);
            assertNotNull("cachedClassFileContent", cachedContent);
            assertSame(content, cachedContent);

            // Rewrite the file to test that FileUtilsCacheinterceptor reads a file that has been modified.
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            cachedContent = fileUtils.read(file.toPath());
            assertNotSame(content, cachedContent);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
