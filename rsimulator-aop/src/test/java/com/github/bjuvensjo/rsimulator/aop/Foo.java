package com.github.bjuvensjo.rsimulator.aop;

import org.springframework.stereotype.Service;

@Service
public interface Foo {

    public String sayHello(String msg);
    
    public String doThrow(String msg) throws BarException;
    
    public void doNotReturn(String msg);
}