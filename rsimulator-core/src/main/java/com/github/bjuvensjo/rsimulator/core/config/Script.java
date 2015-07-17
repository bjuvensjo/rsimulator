package com.github.bjuvensjo.rsimulator.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Script is an annotation that is used for script interceptors.
 *
 * @author Magnus Bjuvensjö
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Script {
}
