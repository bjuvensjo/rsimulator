package org.rsimulator.example.unittest;

public class Greeting {
    private String from;
    private String to;
    private String message;

    public Greeting(String aFrom, String aTo, String aMessage) {
        super();
        this.from = aFrom;
        this.to = aTo;
        this.message = aMessage;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}
