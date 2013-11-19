package org.rsimulator.recorder;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * @author Anders Bälter
 */
@Ignore("path issue when running from pom-modules")
public class RecorderTest {
    private static final int READ_TIMEOUT = 12000;
    private static final String ENCODING = "UTF-8";
    private static final String TXT = ".txt";
    private static final String FILE_PREFIX = "test";

    private Config config = new Config();

    @Test
    public void testRecorder() throws Exception {
        HttpURLConnection con = getConnection("GET", "http://localhost:8888/proxy/test/", null);
        String basePath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String path = new StringBuilder(basePath).append("..").append(File.separator)
                .append("..").append(File.separator).append(config.get(Config.BASE_DIRECTORY))
                .append(File.separator).append("specific_user")
                .append(File.separator)
                .append("test")
                .toString();
        try {
            con.setRequestProperty("user", "specific_user");
            con.getOutputStream().write("test".getBytes(ENCODING));

            con.connect();
            con.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
            String requestFileURI = buildFilePath(path, Constants.REQUEST_FILENAME, TXT);
            File requestFile = new File(requestFileURI);
            String responseFileURI = buildFilePath(path, Constants.RESPONSE_FILENAME, TXT);
            File responseFile = new File(responseFileURI);
            String propertiesFileURI = buildFilePath(path, "", ".properties");
            File propertiesFile = new File(propertiesFileURI);
            Assert.assertTrue(requestFile.exists());
            Assert.assertTrue(responseFile.exists());
            Assert.assertTrue(propertiesFile.exists());
            FileUtils.deleteDirectory(new File(path));
        }
    }

    @Test
    public void testResponseCodeRecording() throws Exception {
        HttpURLConnection con = getConnection("GET", "http://localhost:8888/proxy/test/", null);
        String basePath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String path = new StringBuilder(basePath).append("..").append(File.separator)
                .append("..").append(File.separator).append(config.get(Config.BASE_DIRECTORY))
                .append(File.separator)
                .append("test")
                .toString();
        String actualResponseCode = null;
        try {
            con.getOutputStream().write("test".getBytes(ENCODING));

            con.connect();
            actualResponseCode = String.valueOf(con.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
            String requestFileURI = buildFilePath(path, Constants.REQUEST_FILENAME, TXT);
            File requestFile = new File(requestFileURI);
            String responseFileURI = buildFilePath(path, Constants.RESPONSE_FILENAME, TXT);
            File responseFile = new File(responseFileURI);
            String propertiesFileURI = buildFilePath(path, "", ".properties");
            File propertiesFile = new File(propertiesFileURI);
            Assert.assertTrue(requestFile.exists());
            Assert.assertTrue(responseFile.exists());
            Assert.assertTrue(propertiesFile.exists());
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            String recordedResponseCode = properties.getProperty("responseCode");
            Assert.assertNotNull(recordedResponseCode);
            Assert.assertEquals(actualResponseCode, properties.getProperty("responseCode"));
            FileUtils.deleteDirectory(new File(path));
        }
    }

    private HttpURLConnection getConnection(String method, String url, String contentType) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Content-Type", contentType);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setReadTimeout(READ_TIMEOUT);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        return con;
    }

    private String buildFilePath(String path, String suffix, String fileType) {
        return new StringBuilder(path).append(File.separator).append(FILE_PREFIX)
                .append(suffix).append(fileType).toString();
    }
}
