package com.github.bjuvensjo.rsimulator.test.spock

import groovy.transform.CompileStatic
import org.spockframework.runtime.extension.ExtensionAnnotation
import spock.lang.Shared

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@CompileStatic
@ExtensionAnnotation(ResourcePathExtension.class)
@interface ResourcePath {
    String fixtureDelimiter() default '-'
    boolean simpleClassName() default true
    boolean rootOnly() default false
}

