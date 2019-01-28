package com.github.bjuvensjo.rsimulator.recorder;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * A HttpServletRequestWrapper which buffers data to a byte array
 *
 * @author Anders Bälter
 * @author Magnus Bjuvensjö
 */
public class RecorderServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] buffer;

    RecorderServletRequestWrapper(HttpServletRequest req) throws IOException {
        super(req);
        initByteArrayOutputStream(req.getInputStream());
    }

    private void initByteArrayOutputStream(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int letti;
        while ((letti = is.read(buffer)) > 0)
            out.write(buffer, 0, letti);
        this.buffer = out.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        return new BufferedServletInputStream(in);
    }

    String getRequestBody(String encoding) throws UnsupportedEncodingException {
        return new String(buffer, encoding);
    }
}
