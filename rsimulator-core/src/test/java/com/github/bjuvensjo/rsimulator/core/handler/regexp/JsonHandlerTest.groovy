package com.github.bjuvensjo.rsimulator.core.handler.regexp

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class JsonHandlerTest extends Specification {
    @Shared
    JsonHandler jsonHandler

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        jsonHandler = injector.getInstance(JsonHandler.class)
    }

    @Unroll
    def 'escape #candidate'(String candidate, String expected) {
        when:
        String actual = jsonHandler.escape(candidate, true)
        then:
        actual == expected
        where:
        expected                                                                       | candidate
        'hello'                                                                        | 'hello'
        '\\{"responseControl":\\{"profile":\\{"profileType":"ANONYMOUS_USER"\\}\\}\\}' | '{"responseControl": {"profile": {"profileType": "ANONYMOUS_USER"}}}'
        '\\["Ford","BMW","Fiat"\\]'                                                    | '[ "Ford", "BMW", "Fiat" ]'
        '\\[\\{"id":".*","name":"Privatkon[a-z]{2}","balance":34251.15766987801\\}\\]' | '[{"id":".*","name":"Privatkon[a-z]{2}","balance":34251.15766987801}]'
    }

    def 'complex candidate escape'() {
        when:
        String candidate = '{"firstName": "John","lastName": "Smith","isAlive": true,"age": 27,"address": {"streetAddress": "21 2nd Street","city": "New York","state": "NY","postalCode": "10021-3100"},"phoneNumbers": [                 {                 "type": "home",                 "number": "212 555-1234"                 },                 {                 "type": "office",                 "number": "646 555-4567"                 },                 {                 "type": "mobile",                 "number": "123 456-7890"                 }                 ],"children": [],"spouse": null}'
        String expected = '\\{"firstName":"John","lastName":"Smith","isAlive":true,"age":27,"address":\\{"streetAddress":"21 2nd Street","city":"New York","state":"NY","postalCode":"10021-3100"\\},"phoneNumbers":\\[\\{"type":"home","number":"212 555-1234"\\},\\{"type":"office","number":"646 555-4567"\\},\\{"type":"mobile","number":"123 456-7890"\\}\\],"children":\\[\\],"spouse":null\\}'
        String actual = jsonHandler.escape(candidate, true)
        then:
        actual == expected
    }
}
