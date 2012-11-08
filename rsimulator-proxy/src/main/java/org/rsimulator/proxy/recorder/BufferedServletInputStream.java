package org.rsimulator.proxy.recorder;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

/**
 * @author Anders Bälter
 */
public class BufferedServletInputStream extends ServletInputStream {

    ByteArrayInputStream bais;

    public BufferedServletInputStream(ByteArrayInputStream bais) {
        this.bais = bais;
    }

    public int available() {
        return bais.available();
    }

    public int read() {
        return bais.read();
    }

    public int read(byte[] buf, int off, int len) {
        return bais.read(buf, off, len);
    }

}