package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test7.
 *
 * @author Magnus Bjuvensjö
 */
public class TestJsonHandler {
    private JsonHandler jsonHandler;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        jsonHandler = injector.getInstance(JsonHandler.class);
    }

    @Test
    public void testEscape() {
        String jsonRequest = "{\"firstName\": \"John\",\"lastName\": \"Smith\",\"isAlive\": true,\"age\": 27,\"address\": {\"streetAddress\": \"21 2nd Street\",\"city\": \"New York\",\"state\": \"NY\",\"postalCode\": \"10021-3100\"},\"phoneNumbers\": [                 {                 \"type\": \"home\",                 \"number\": \"212 555-1234\"                 },                 {                 \"type\": \"office\",                 \"number\": \"646 555-4567\"                 },                 {                 \"type\": \"mobile\",                 \"number\": \"123 456-7890\"                 }                 ],\"children\": [],\"spouse\": null}";

        assertEquals("hello", jsonHandler.escape("hello", true));
        assertEquals("\\{\"responseControl\":\\{\"profile\":\\{\"profileType\":\"ANONYMOUS_USER\"\\}\\}\\}", jsonHandler.escape("{\"responseControl\":{\"profile\":{\"profileType\":\"ANONYMOUS_USER\"}}}", true));
        assertEquals("\\[\"Ford\",\"BMW\",\"Fiat\"\\]", jsonHandler.escape("[ \"Ford\", \"BMW\", \"Fiat\" ]", true));
        assertEquals("\\[\\{\"id\":\".*\",\"name\":\"Privatkon[a-z]{2}\",\"balance\":34251.15766987801\\}\\]", jsonHandler.escape("[{\"id\":\".*\",\"name\":\"Privatkon[a-z]{2}\",\"balance\":34251.15766987801}]", true));
        assertEquals("\\{\"firstName\":\"John\",\"lastName\":\"Smith\",\"isAlive\":true,\"age\":27,\"address\":\\{\"streetAddress\":\"21 2nd Street\",\"city\":\"New York\",\"state\":\"NY\",\"postalCode\":\"10021-3100\"\\},\"phoneNumbers\":\\[\\{\"type\":\"home\",\"number\":\"212 555-1234\"\\},\\{\"type\":\"office\",\"number\":\"646 555-4567\"\\},\\{\"type\":\"mobile\",\"number\":\"123 456-7890\"\\}\\],\"children\":\\[\\],\"spouse\":null\\}", jsonHandler.escape(jsonRequest, true));
    }
}
