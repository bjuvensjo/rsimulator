package com.github.bjuvensjo.rsimulator.recorder;

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
 * @author Magnus Bjuvensjö
 */
public class RecorderServletResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
    private int httpStatus;

    RecorderServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return pw.getStream();
    }

    @Override
    public PrintWriter getWriter() {
        return pw.getWriter();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        super.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    int getStatus() {
        return httpStatus;
    }

    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    String getResponseBody(String encoding) throws UnsupportedEncodingException {
        return pw.toString(encoding);
    }

    byte[] getBytes() {
        return pw.getBytes();
    }
}
