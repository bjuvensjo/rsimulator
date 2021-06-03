package com.github.bjuvensjo.rsimulator.aop;

public class DummyFoo implements Foo {

    @Override
    public String sayHello(String msg) {
        return null;
    }

    @Override
    public String doThrow(String msg) throws BarException {
        return null;
    }

    @Override
    public void doNotReturn(String msg) {

    }
}
