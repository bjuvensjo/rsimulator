package com.github.bjuvensjo.rsimulator.jms;

public class Encoder {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private String encoding = DEFAULT_ENCODING;

    public String encode(String body) throws Exception {
        return new String(body.getBytes(DEFAULT_ENCODING), encoding);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
