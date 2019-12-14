package com.github.bjuvensjo.rsimulator.recorder;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * A ServletInputStream which reads from a ByteArrayInputStream
 */
public class BufferedServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream bis;

    BufferedServletInputStream(ByteArrayInputStream bis) {
        this.bis = bis;
    }

    /**
     * @see ByteArrayInputStream#read()
     */
    public int read() {
        return bis.read();
    }

    /**
     * @see ByteArrayInputStream#read(byte[], int, int)
     */
    public int read(byte[] buffer, int off, int len) {
        return bis.read(buffer, off, len);
    }

    /**
     * @see ByteArrayInputStream#available()
     */
    public int available() {
        return bis.available();
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }
}