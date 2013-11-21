package org.rsimulator.recorder;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

/**
 * A ServletInputStream which reads from a ByteArrayInputStream
 *
 * @author Anders Bälter
 */
public class BufferedServletInputStream extends ServletInputStream {

    ByteArrayInputStream bais;

    public BufferedServletInputStream(ByteArrayInputStream bais) {
        this.bais = bais;
    }

    /**
     * @see ByteArrayInputStream#available()
     */
    public int available() {
        return bais.available();
    }

    /**
     * @see ByteArrayInputStream#read()
     */
    public int read() {
        return bais.read();
    }

    /**
     * @see ByteArrayInputStream#read(byte[], int, int)
     */
    public int read(byte[] buf, int off, int len) {
        return bais.read(buf, off, len);
    }

}