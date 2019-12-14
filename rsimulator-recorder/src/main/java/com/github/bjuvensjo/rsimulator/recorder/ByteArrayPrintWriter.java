package com.github.bjuvensjo.rsimulator.recorder;

import jakarta.servlet.ServletOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * A PrintWriter and ServletOutputStream decorator which shares a ByteArrayOutputStream
 */
class ByteArrayPrintWriter {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private final PrintWriter pw = new PrintWriter(bos);
    private final ServletOutputStream sos = new ByteArrayServletStream(bos);

    PrintWriter getWriter() {
        return pw;
    }

    ServletOutputStream getStream() {
        return sos;
    }

    String toString(String encoding) throws UnsupportedEncodingException {
        return bos.toString(encoding);
    }

    byte[] getBytes() {
        return bos.toByteArray();
    }
}