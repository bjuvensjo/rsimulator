#rsimulator-core
The rsimulator-core encapsulates all core simulation logic.

It is typically used only from other modules of the rsimulator such as [[rsimulator-aop]] and [[rsimulator-http]] that enables simulation through AOP and HTTP respectively.

It should be used if the rsimulator should be extended to support other "protocols", e.g. Socket connections, MQ etc.

* <a href="#Simulator">Simulator</a>
* <a href="#Test data">Test data</a>
    * <a href="#Structure and Naming">Structure and Naming</a>
    * <a href="#Request Matching">Request Matching</a>
    * <a href="#Response Replacement">Response Replacement</a>
    * <a href="#Properties">Properties</a>
    * <a href="#Encoding">Encoding</a>
    * <a href="#Test data generation">Test data generation</a>
* <a href="#Scripting">Scripting</a>
* <a href="#Configuration">Configuration</a>
* <a href="#Hot deploy">Hot deploy</a>

<a name="Simulator">
##Simulator
The Simulator interface is the public interface of the rsimulator-core.
Its one public method is declared:

```java
    SimulatorResponse service(String rootPath, String rootRelativePath, String request, String contentType) 
        throws IOException;
```

where

1. rootPath designates the path where to recursively search for test data that match the specified request
2. rootRelativePath is a path relative to the root which if specified limits the search to rootPath/rootRelativePath
3. request is an incoming request for which a matching will be searched
4. contentType is the content type of the request. Currently supported content types are txt and xml
4. and the SimulatorResponse contains the response string and associated properties

<a name="Test data">
##Test data
<a name="Structure and Naming">
###Structure and Naming
The test data is stored in ordinary files and folders.

The root folder is the rootPath.
The purpose of the rootPath is to make it possible to work with independent sets of test data, e.g. one set to be used in development and another one in a demo application and possibly different sets for different test users.

The folder structure below the rootPath have a correspondance to the rootRelativePath. If for instance the Simulator is called with

```java
    service("DEV", "Payment/Domestic", "SomeRequestString", "txt"); 
```

the Simulator will search recursively for a match in the folder DEV/Payment/Domestic and its subfolders.

The purpose of the recursive search is to make it possible to structure the test data, e.g. according test case, test user or service version.
The purpose of the rootRelativePath is to limit the search area and thus avoid possible mutiple matches (and improve performance).
See [[rsimulator-aop]] and [[rsimulator-http]]) for further information of how rootPath and rootRelativePath can be used.

The test data files are request-response pairs and optionally also a properties file per pair. Such files must be in the same folder and follow the name conventions:

* Request -  ".*Request\\.[a-zA-Z]+$", e.g. GetDomesticPayment-200806-1-Request.xml, GetCustomer-200806-01-Request.txt
* Response - Exactly the same as the corresponding request file with "Request" replaced with "Response", e.g. GetDomesticPayment-200806-1-Response.xml, GetCustomer-200806-01-Response.txt
* Properties - Exactly the same as the corresponding request file with "Request" replaced with "" and the file extension properties, e.g. GetDomesticPayment-200806-1-.properties, GetCustomer-200806-01-.properties

<a name="Request Matching">
###Request Matching
The rsimulator-core recursively searches for a test data request matching the incoming request in the rootPath/rootRealtivePath folder and its subfolders. The response paired with the *first* matching request will be returned.

The rsimulator-core supports [[regular expressions|http://download.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html]] in the test data request. e.g. an incoming request

```xml    
    <Request>
        <UserId>197903081029<UserId>
        <Name>Harald Ljungstroem<Name>
        <Timestamp>20080718102053123<Timestamp>
    </Request>
```

    matches

```xml
    <Request>
        <UserId>197903081029<UserId>
        <Name>H[a-z]+ Ljungstroem<Name>
        <Timestamp>.*<Timestamp>
   </Request>    
```

Except for regular expressions and non-relevant spaces in XML documents, the match must be exact.

Regular expressions is the central matching mechanism of the rsimulator-core and the power of regular expressions can handle, if not all, the vast majority of matching needs.
One advantage of using the regular expression mechanism is that test data could be added simply by adding only the test data request and response files.
However, <a href="#Scripting">Scripting</a> can be used to complement the regular expression mechanism, especially for adding behaviour that is not matching.

*Tip: Use regular expression wildcards with some caution...*

<a name="Response Replacement">
###Response Replacement
The rsimulator-core supports regular expression capturing groups, e.g. an incoming request

```text
    Harald Ljungstroem says hello!    
```

that matches the request

```text
    ([a-zA-Z]{6}) ([^ ]+) says hello!    
```

paired with the response

```text
    Hello ${1} ${2} says rsimulator!    
```

will return

```text
    Hello Harald Ljungstroem says rsimulator!    
```

<a name="Properties">
### Properties
Properties can be used to add characteristics to a test that is not test data related.
Typically, the use of them is a convenient way to achieve what could otherwise have been done with <a href="#Scripting">Scripting</a>.

The properties supported by rsimulator-core are

* delay - a time in ms that the rsimulator-core should delay the response

See [[rsimulator-http]] and [[rsimulator-aop]] for support of other properties.

<a name="Encoding">
###Encoding
All test data files must be UTF-8 encoded.

<a name="Test data generation">
###Test data generation
Some tips for generating test data:

1. Use already existing request-responses of the same type as templates
2. If Web Services, generate from within soapUI
3. IF XML, generate from within Eclipse or similar
4. For all request, a log file
5. Record through rsimulator utils (will be added in future version)

Of course, above is only tips. Use whatever means that suits the needs the best.

<a name="Scripting">
##Scripting
The rsimulator-core supports extension points through scripts invoced from the SimulatorScriptInterceptor.

The SimulatorScriptInterceptor is an interceptor that supports Groovy scripts intercepting invocations of
the <a href="#Simulator">Simulator</a> service method.

The following scripts are supported:

* GlobalRequest.groovy; Must be put in the rootPath folder and is applied before the service method invocation
* &lt;TestName&gt;.groovy; Must be put in the same folder as test request and response and is applied directly after the service method invocation
    * The name of the groovy file must be the same as for the test request, e.g. Test1Request.txt - Test1.groovy
* GlobalResponse.groovy; Must be put in the rootPath folder and is applied secondly after the service method invocation

All script have a Map<String, Object> available through the variable vars. The keys

* contentType
* simulatorResponse
* request
* rootPath
* rootRelativePath

can be used to access invocation arguments and return value.
In addition, the map can be used to communicate arbitrary objects between the Groovy scripts.

If a script sets a SimulatorResponse in the vars map, this SimulatorResponse is directly returned.

<a name="Configuration">
##Configuration
The rsimulator-core distribution includes a rsimulator-default.properties file containing the property:

```text
    simulatorCache=false
```

The rsimulator-default.properties can be overridden by placing a rsimulator.properties file in the root of the classpath.
Of course, the only reason for doing this is to enable simulatorCache, i.e.

```text
    simulatorCache=true
```

From a usage perspective, the effect of this configuration is that the <a href="#Hot deploy">Hot deploy</a> is disabled and the rsimulator-core performance is boosted.
A possible use case for this configuration could be performance tests.

<a name="Hot deploy">
##Hot deploy
All changes of test data and configuration are hot deployed, i.e. no restart of anything is needed.
Nevertheless, one needs to be aware that this behaviour can be disabled through <a href="#Configuration">Configuration</a>.
