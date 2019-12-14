package com.github.bjuvensjo.rsimulator.recorder;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;

/**
 * A ServletOutputStream which writes to a ByteArrayOutputStream
 */
public class ByteArrayServletStream extends ServletOutputStream {
    private final ByteArrayOutputStream out;

    ByteArrayServletStream(ByteArrayOutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int param) {
        out.write(param);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException();
    }
}