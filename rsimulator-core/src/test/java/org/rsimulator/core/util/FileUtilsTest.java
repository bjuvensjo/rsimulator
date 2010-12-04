package org.rsimulator.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.DIModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FileUtilsTest {
    private FileUtils fileUtils;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new DIModule());
        fileUtils = injector.getInstance(FileUtils.class);
    }

    @Test
    public void testFindRequests() {
        List<File> xmlRequests = fileUtils.findRequests(new File(getClass().getResource("/").getPath()), "xml");
        assertTrue("xmlRequests", xmlRequests.size() > 0);
        for (File file : xmlRequests) {
            assertTrue("xmlFile", file.getName().endsWith(".xml"));
        }
        List<File> txtRequests = fileUtils.findRequests(new File(getClass().getResource("/").getPath()), "txt");
        assertTrue("txtRequests", txtRequests.size() > 0);
        for (File file : txtRequests) {
            assertTrue("txtFile", file.getName().endsWith(".txt"));
        }
    }

    @Test
    public void testRead() {
        String resource = "/test1/Test.properties";
        try {
            File file = new File(getClass().getResource(resource).getPath());
            String content = fileUtils.read(file);
            String cachedContent = fileUtils.read(file);
            assertNotNull("classFileContent", content);
            assertNotNull("cachedClassFileContent", cachedContent);
            assertSame(content, cachedContent);

            // Rewrite the file to test that FileUtilsCacheinterceptor reads a file that has been modified.
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            cachedContent = fileUtils.read(file);
            assertNotSame(content, cachedContent);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
