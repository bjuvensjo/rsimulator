package com.github.bjuvensjo.rsimulator.jms.config;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * JmsModule holds Guice configurations.
 *
 * @author Magnus Bjuvensjö
 */
public class JmsModule extends AbstractModule {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(new TypeLiteral<String>() {
        }).annotatedWith(Names.named("simulatorContentType")).toInstance("txt");

//        bind(new TypeLiteral<Integer>() {
//        }).annotatedWith(Names.named("headerLength")).toInstance(48);
//
//        bind(new TypeLiteral<Integer>() {
//        }).annotatedWith(Names.named("headerBodyLengthBeginIndex")).toInstance(4);
//
//        bind(new TypeLiteral<Integer>() {
//        }).annotatedWith(Names.named("headerBodyLengthEndIndex")).toInstance(8);
    }
}
