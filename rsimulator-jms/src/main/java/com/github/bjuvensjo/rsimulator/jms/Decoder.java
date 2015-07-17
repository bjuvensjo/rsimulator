package com.github.bjuvensjo.rsimulator.jms;

public class Decoder {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private String encoding = DEFAULT_ENCODING;

    public String decode(String body) throws Exception {
        return new String(body.getBytes(encoding), DEFAULT_ENCODING);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
