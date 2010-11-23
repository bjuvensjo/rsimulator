package org.rsimulator.example.unittest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * WebServiceClientImpl.
 *
 * @author Magnus Bjuvensjö
 */
public class WebServiceClientImpl implements WebServiceClient {
    private static final int BUFFER_SIZE = 200;
    private static final int READ_TIMEOUT = 10000;
    private static final String ENCODING = "UTF-8";
    private ResourceBundle bundle = ResourceBundle.getBundle(WebServiceClientImpl.class.getName());
    private String path = "/the/path/of/the/webservice";

    /**
     * {@inheritDoc}
     */
    @Override
    public String greet(Greeting greeting) throws IOException {
        String result = null;
        String url = new StringBuilder().append("http://").append(bundle.getString("host")).append(":")
                .append(bundle.getString("port")).append(path).toString();
        HttpURLConnection con = null;
        try {
            con = getConnection("POST", url, "application/soap+xml");
            String request = getRequest(greeting);
            con.getOutputStream().write(request.getBytes(ENCODING));
            String response = read(con.getInputStream());
            result = parseResponse(response);
        } finally {
            con.disconnect();
        }
        return result;
    }

    private String getRequest(Greeting greeting) {
        return new StringBuilder().append("<xml>").append("<from>").append(greeting.getFrom()).append("</from>")
                .append("<to>").append(greeting.getTo()).append("</to>").append("<message>")
                .append(greeting.getMessage()).append("</message>").append("</xml>").toString();
    }

    private String parseResponse(String response) {
        return response.replaceAll("<xml>", "").replaceAll("</xml>", "");
    }

    private HttpURLConnection getConnection(String method, String url, String contentType) throws IOException {
        HttpURLConnection con = null;
        con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", contentType);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setReadTimeout(READ_TIMEOUT);
        return con;
    }

    private String read(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = is.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, n, ENCODING));
        }
        return sb.toString();
    }
}
