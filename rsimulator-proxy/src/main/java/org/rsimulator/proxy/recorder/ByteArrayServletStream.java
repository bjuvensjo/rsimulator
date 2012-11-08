package org.rsimulator.proxy.recorder;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Anders Bälter
 */
public class ByteArrayServletStream extends ServletOutputStream {

    ByteArrayOutputStream baos;

    ByteArrayServletStream(ByteArrayOutputStream baos) {
        this.baos = baos;
    }

    @Override
    public void write(int param) throws IOException {
        baos.write(param);
    }
}