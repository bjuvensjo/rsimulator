package com.github.bjuvensjo.rsimulator.recorder;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;

/**
 * A ServletOutputStream which writes to a ByteArrayOutputStream
 *
 * @author Anders Bälter
 */
public class ByteArrayServletStream extends ServletOutputStream {
    private ByteArrayOutputStream out;

    ByteArrayServletStream(ByteArrayOutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int param) {
        out.write(param);
    }
}