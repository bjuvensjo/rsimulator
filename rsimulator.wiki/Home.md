#Home

##Overview
The rsimulator simulates Enterprise Information Systems (EIS)/"backend systems" and interface/class implementations.
Its primary purpose is to be an easy to use lightweight development tool promoting productivity.
Additionally, it can be used to performance tests and as a simulator of backend systems for a demo application.

The central module is the

* [[rsimulator-core]]

This is not used directly, but indirectly through the direct use of 

* [[rsimulator-aop]]
* [[rsimulator-http]]

that makes the rsimulator-core features available through http and aop mechanisms respectively.

There following modules are intended to exemplify the use of the [[rsimulator-aop]] and [[rsimulator-http]]

* [[rsimulator-example-unittest]]
* [[rsimulator-example-webapp]]

## Resources
* [[Getting started]]
* [[Why rsimulator]]
* [[Documentation]]
* [[License]]
