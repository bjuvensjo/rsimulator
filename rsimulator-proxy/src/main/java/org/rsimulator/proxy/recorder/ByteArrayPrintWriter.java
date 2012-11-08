package org.rsimulator.proxy.recorder;


import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Anders Bälter
 */
public class ByteArrayPrintWriter {

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private PrintWriter pw = new PrintWriter(baos);

    private ServletOutputStream sos = new ByteArrayServletStream(baos);

    public PrintWriter getWriter() {
        return pw;
    }

    public ServletOutputStream getStream() {
        return sos;
    }

    public String toString(String encoding) throws UnsupportedEncodingException {
        return baos.toString(encoding);
    }

    public byte[] getBytes() {
        return baos.toByteArray();
    }
}