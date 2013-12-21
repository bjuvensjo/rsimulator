#rsimulator-aop
The main purpose of rsimulator-aop is to make the [[rsimulator-core]] functionality available through AOP interception.
Thus, it encapsulates all AOP specific simulation logic.

It is typically used to simulate an interface or class invocation.

Typical use cases are unit, system and performance tests as well as providing a demo application with simulated Enterprise Information Systems (EIS) providing Java interfaces.

* <a href="#AopAllianceSimulator">AopAllianceSimulator</a>
* <a href="#AspectJSimulatorAdapter">AspectJSimulatorAdapter</a>
* <a href="#Test data">Test data</a>
* <a href="#Properties">Properties</a>
* <a href="#Configuration">Configuration</a>
    * <a href="#Root path">Root path</a>
    * <a href="#Root relative path">Root relative path</a>

<a name="AopAllianceSimulator">
##AopAllianceSimulator
The AopAllianceSimulator is one of the two public interfaces of the rsimulator-aop.

It supports simulation through AOP alliance compliant interceptions, e.g. Guice and Spring Framework interceptors.
When intercepting interfaces, no interface implementation is needed since the interface implementation is provided by the AoPAllianceSimulator implementation together with simulation test data. At least this is true when using Spring Framework interceptors. When using Guice interceptors, a "dummy" interface implementation seems to be needed.

Regarding the [[rsimulator-core]] request, simulatorResponse and contentType parameters, see <a href="#Test data">Test data</a>.

Regarding the [[rsimulator-core]] rootPath and rootRelativePath parameters, see <a href="#Configuration">Configuration</a>.

<a name="AspectJSimulatorAdapter">
##AspectJSimulatorAdapter
The AspectJSimulatorAdapter is one of the two public interfaces of the rsimulator-aop.

It supports simulation through AspectJ interceptions.
When intercepting interfaces, no interface implementation is needed since the interface implementation is provided by the AspectJSimulatorAdapter implementation together with simulation test data.

Regarding the [[rsimulator-core]] request, simulatorResponse and contentType parameters, see <a href="#Test data">Test data</a>.

Regarding the [[rsimulator-core]] rootPath and rootRelativePath parameters, see <a href="#Configuration">Configuration</a>.

<a name="Test data">
##Test data
The rsimulator-aop invokes the [[rsimulator-core]] with an xml representation of the method invocation parameters by utilizing [[XStream|http://xstream.codehaus.org]].
The xml representation of the parameters is that of XStream, with the following addition

* a request (method parameters) is surrounded by a "request" tag, e.g. <br/>```<request><string>Hello from rsimulator-aop</string></request>```

When the rsimulator-aop recieves a SimulatorResponse from the [[rsimulator-core]] it expects it to be a equivalent xml representation, with the following addition

* response (method return value) is surrounded by a "response" tag, e.g. <br/>```<response><string>Hello rsimulator-aop from rsimulator-core</string></response>```

The test data requests of the above examples could for instance be

* request - ```<request><string>Hello from (rsimulator-.*)</string></request>```
* response - ```<response><string>Hello ${1} from rsimulator-core</string></response>```

The rsimulator-aop will always invoke rsimulator-core with with xml as value of the contentType parameter.

<a name="Properties">
##Properties
Properties can be used to add characteristics to a test that is not test data related.
Typically, the use of them is a convenient way to achieve what could otherwise have been done with <a href="rsimulator-core#Scripting">Scripting</a>.

The rsimulator-aop does not support any properties of its own.

See <a href="rsimulator-core#Properties">rsimulator-core Properties</a> for support of other properties.

<a name="Configuration">
##Configuration

<a name="Root path">
###Root path
The rootPath is configured through 

* a property on the AopAllianceSimulator
* a method parameter on the AspectJSimulatorAdapter 

When utilizing rsimulator-aop for unit tests, the most convenient way is to utilize provided methods that sets the rootPath to the directory that contains the compiled test class, given that the test data is structured so that is is placed in the same directory or a subdirectory.

<a name="Root relative path">
###Root relative path
The root relative path is configured through

* a property on the AopAllianceSimulator (disabled by default)
* a method parameter on the AspectJSimulatorAdapter 

To configure true, implies that the rsimulator-aop will invoke the [[rsimulator-core]] with a rootRelativePath created of the intercepted class canonical name and the name of the intercepted method. For instance, if the rsimulator-aop intercepts a method pay of the payment.PaymentService interface, the rootRelativePath value will be payment/PaymentService/pay. The enablement of rootRelativePath is recommended in all usages except for unit testing, where it is better to dynamically configure the rootPath to the specific test data folder of the test case.