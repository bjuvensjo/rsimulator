package com.github.bjuvensjo.rsimulator.aop;

import org.springframework.stereotype.Service;

@Service
public interface Foo {

    String sayHello(String msg);

    String doThrow(String msg) throws BarException;

    void doNotReturn(String msg);
}