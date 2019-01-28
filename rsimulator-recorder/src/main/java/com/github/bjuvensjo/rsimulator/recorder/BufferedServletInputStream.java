package com.github.bjuvensjo.rsimulator.recorder;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

/**
 * A ServletInputStream which reads from a ByteArrayInputStream
 *
 * @author Anders Bälter
 */
public class BufferedServletInputStream extends ServletInputStream {
    private ByteArrayInputStream bis;

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
}