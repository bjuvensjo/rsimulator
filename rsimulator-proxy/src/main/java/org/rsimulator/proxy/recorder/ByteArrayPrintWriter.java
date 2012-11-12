package org.rsimulator.proxy.recorder;


import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * A PrintWriter and ServletOutputStream decorator which shares a ByteArrayOutputStream
 *
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

    /**
     * Get the byte array as a String
     * @param encoding
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String toString(String encoding) throws UnsupportedEncodingException {
        return baos.toString(encoding);
    }

    public byte[] getBytes() {
        return baos.toByteArray();
    }
}