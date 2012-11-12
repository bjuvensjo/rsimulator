package org.rsimulator.proxy.recorder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * A HttpServletResponseWrapper which gets its ServletOutputStream from a ByteArrayPrintWriter
 *
 * @author Anders Bälter
 */
public class RecorderServletResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayPrintWriter pw = new ByteArrayPrintWriter();

    public RecorderServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return pw.getStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return pw.getWriter();
    }

    public String getResponseAsString(String encoding) throws UnsupportedEncodingException {
        return pw.toString(encoding);
    }

    public byte[] getBytes() {
        return pw.getBytes();
    }
}
