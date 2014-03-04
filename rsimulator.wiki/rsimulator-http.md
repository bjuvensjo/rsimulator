#rsimulator-http
The main purpose of rsimulator-http is to make the [[rsimulator-core]] functionality available over http(s).
Thus, it encapsulates all http specific simulation logic.

It is typically used to simulate an ESB and multiple service providers, possibly providing heterogeneous services over http such as SOAP, XML and fixed length text.
(Support for restful services will probably be added in future version.)

Typical use cases are unit, system and performance tests as well as providing a demo application with simulated Enterprise Information Systems (EIS).

* <a href="#HttpSimulator">HttpSimulator</a>
* <a href="#Properties">Properties</a>
* <a href="#Configuration">Configuration</a>
    * <a href="#Default application">Default application</a>
    * <a href="#Root path">Root path</a>
    * <a href="#Root relative path">Root relative path</a>
* <a href="#Content type">Content type</a>
* <a href="#Encoding">Encoding</a>
* <a href="#Example of Maven Jetty configuration">Example of Maven Jetty configuration</a>

<a name="HttpSimulator">
##HttpSimulator
The HttpSimulator servlet is to be seen as the public interface of the rsimulator-http.

It supports simulation through the http POST (and GET) methods.

* For a POST request, the complete request body is sent to the [[rsimulator-core]] as request parameter
* For a GET request, the value of the  parameter "request" is sent to the [[rsimulator-core]] as request parameter

The HttpSimulator writes the response field value of the SimulatorResponse returned from the [[rsimulator-core]] as http response.

Regarding the [[rsimulator-core]] rootPath and rootRelativePath parameters, see <a href="#Configuration">Configuration</a>

<a name="Properties">
##Properties
Properties can be used to add characteristics to a test that is not test data related.
Typically, the use of them is a convenient way to achieve what could otherwise have been done with <a href="rsimulator-core#Scripting">Scripting</a>.

The properties supported by rsimulator-http are

* responseCode - a http response code, e.g. 500 to simulate a internal server error

See <a href="rsimulator-core#Properties">rsimulator-core Properties</a> for support of other properties.

<a name="Configuration">
##Configuration

<a name="Default application">
###Default application
The recommendation is to deploy the rsimulator-http as the default web application on some lightweight web server, e.g. Jetty, i.e. it should be deployed with "/" as application context.

The main advantage of deploying it as the default web application is that the application that are tested at most needs to configure host and port to use the rsimulator-http. Except for this configuration, the simulation can be completely transparent to the tested application. And typically, applications that call services anyhow need to support configuration of different hosts and ports for different environments.

To give an example. If the tested application calls two services

1. http://<rsimulator-http-host>:<rsimulator-http-port>/Payment/DomesticPayment/201012
2. http://<rsimulator-http-host>:<rsimulator-http-port>/Account/CashAccount/200801

the rsimulator-http will recieve both request even though their contexts are different.

<a name="Root path">
###Root path
The rsimulator-core has a default root path defined as

```java
DEFAULT_ROOT_PATH = "src/main/resources"
```

that typically suits well if the rsimulator-http is used from a Maven project that has structured its rsimulator test data in this folder.

The default root path can be overridden by configuring a rootPath system property, e.g. -DrootPath=src/test/resources.

In addition, to enable to keep test data together with unit tests, the rootPath can be dynamically configured through a http GET request with a rootPath parameter, e.g. http://<rsimulator-http-host>:<rsimulator-http-port>/?rootPath="src/test/resources". The rsimulator-http provides the HttpSimulatorConfig convenience class for making this configuration an ease. The drawback of this way of configuration is that it is not thread safe, i.e. if two unit tests in parallel first both perform a configuration against the same rsimulator-http instance and then execute the actual tests, they will both be running against the lastly performed configuration. However, if tests including configuration, are executed sequentually there will be no problem. The benefit of the configuration is that it can be transparent to the tested unit.

<a name="Root relative path">
###Root relative path
By default, the use of a root relative path is disabled.

The default disablement can be overridden by configuring a useRootRelativePath system property, e.g. -DuseRootRelativePath=true

To configure true, implies that the HttpSimulator will invoke the [[rsimulator-core]] with the context relative path of the http request url as rootRelativePath. For instance, if the HttpSimulator recieves a http request with the URL http://<rsimulator-http-host>:<rsimulator-http-port>/Payment/DomesticPayment/201012 the [[rsimulator-core]] will be invoked with /Payment/DomesticPayment/201012 as value of the rootRelativePath parameter. The enablement of rootRelativePath is recommended in all usages except for unit testing, where it is better to dynamically configure the rootPath to the specific test data folder of the test case.

In addition, the useRootRelativePath can be dynamically configured through a http GET request with a useRootRelativePath parameter, e.g. http://<rsimulator-http-host>:<rsimulator-http-port>/?rootPath="src/test/resources&useRootRelativePath=true". The rsimulator-http provides the HttpSimulatorConfig convenience class for making this configuration an ease. What is said regarding thread safeness in the rootPath section, holds here as well.

<a name="Content type">
##Content type
The rsimulator-http supports content types specified as http header content type value, e.g. Content-Type: text/xml
Thus, an incoming http request that specifies an content type will recieve a http response with the same content type.

The rsimulator-http performs the following mapping of http content types to [[rsimulator-core]] content types according to the following code snippet from the HttpModule class:

```java
    Map<String, String> contentTypes = new HashMap<String, String>();
    contentTypes.put("application/xml", "xml");
    contentTypes.put("application/soap+xml", "xml");
    contentTypes.put("text/xml", "xml");
    contentTypes.put("default", "txt");
```

Note that the default mapping is txt, i.e. if the http header content type value is none of the explicitly specified it will map to txt.
Mappings could be added by modifying the HttpModule class. 

<a name="Encoding">
##Encoding
The rsimulator-http supports encoding specified as http header charset value, e.g. Content-Type: text/xml; charset=utf-8.
Thus, an incoming http request that specifies an encoding will recieve a http response with the same encoding. 

Nevertheless, test data files must all be UTF-8 encoded, see [[rsimulator-core]].

<a name="Example of Maven Jetty configuration">
##Example of Maven Jetty configuration
Below is an example of how to configure the rsimulator-http with Maven and Jetty.

```xml
    <plugin>
        <groupId>org.mortbay.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
        <configuration>
            <scanIntervalSeconds>0</scanIntervalSeconds>
            <webAppConfig>
                <descriptor>${basedir}/src/test/resources/web.xml</descriptor>              
                <contextPath>/</contextPath>
            </webAppConfig>
            <webAppSourceDirectory>${basedir}/src/test/resources</webAppSourceDirectory>
            <systemProperties>
                <systemProperty>
                    <name>rootPath</name>
                    <value>${basedir}/src/test/resources</value>
                </systemProperty>
                <systemProperty>
                    <name>useRootRelativePath</name>
                    <value>true</value>
                </systemProperty>
            </systemProperties>
            <stopKey>foo</stopKey>
            <stopPort>9999</stopPort>
        </configuration>
        <executions>
            <execution>
                <id>start-jetty</id>
                <phase>test-compile</phase>
                <goals>
                    <goal>run</goal>
                </goals>
                <configuration>
                    <daemon>true</daemon>
                </configuration>
            </execution>
            <execution>
                <id>stop-jetty</id>
                <phase>test</phase>
                <goals>
                    <goal>stop</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
```