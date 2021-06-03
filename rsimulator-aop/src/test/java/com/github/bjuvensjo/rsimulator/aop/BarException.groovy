package com.github.bjuvensjo.rsimulator.aop

class BarException extends Exception {
    String code

     BarException(String code, String message) {
        super(message)
        this.code = code
    }
}
