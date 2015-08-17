# Usage

## Example

### Java
Implement a class

    package com.acompany;
    
    import org.apache.activemq.broker.BrokerService;
    import org.apache.camel.CamelContext;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    public class ACompanyJmsSimulator {
    
        public static void main(String[] args) {
            Logger log = LoggerFactory.getLogger(ACompanyJmsSimulator.class);
            try {
                // Suppress ehcache update check (just to avoid an exception in the log...)
                System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");

                // Start ActiveMQ
                BrokerService broker = new BrokerService();
                // configure the broker
                broker.setBrokerName("entra");
                broker.addConnector("tcp://localhost:61616");
                broker.start();
                log.info("ActiveMQ started");
    
                // Start Camel
                ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
                CamelContext camelContext = ctx.getBean(CamelContext.class);
                camelContext.start();
                log.info("CamelContext started");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

### Spring
Create a Spring configuration

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:camel="http://camel.apache.org/schema/spring"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
           http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

        <bean class="com.github.bjuvensjo.rsimulator.jms.JmsSimulator">
            <property name="jms" value="activemq" />
            <property name="queue" value="outQueue" />
            <property name="replyTo" value="inQueue" />
            <property name="simulatorContentType" value="txt" />
            <property name="decoder">
                <bean class="com.github.bjuvensjo.rsimulator.jms.Decoder">
                    <property name="encoding" value="UTF-8"/>
                </bean>
            </property>
            <property name="encoder">
                <bean class="com.github.bjuvensjo.rsimulator.jms.Encoder">
                    <property name="encoding" value="UTF-8"/>
                </bean>
            </property>
        </bean>

        <camel:camelContext useMDCLogging="true" streamCache="true">
            <camel:contextScan/>
            <camel:jmxAgent id="agent" disabled="true"/>
        </camel:camelContext>
    </beans>

### Maven
Create a Maven configuration

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.acompany</groupId>
        <artifactId>rsimulator-acompany</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <packaging>jar</packaging>
        <name>rsimulator-acompany</name>
    
        <properties>
            <rsimulator.version>1.1.0-SNAPSHOT</rsimulator.version>
            <activemq.version>5.8.0</activemq.version>
        </properties>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.codehaus.mojo</groupId>
                    <artifactId>exec-maven-plugin</artifactId>
                    <version>1.2.1</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>java</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <mainClass>com.acompany.ACompanyJmsSimulator</mainClass>
                        <systemProperties>
                            <systemProperty>
                                <key>rootPath</key>
                                <value>${basedir}/src/main/resources/utv</value>
                            </systemProperty>
                        </systemProperties>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>commons-lang</groupId>
                <artifactId>commons-lang</artifactId>
                <version>2.6</version>
            </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-camel</artifactId>
            <version>${activemq.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.camel</groupId>
                    <artifactId>camel-jms</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-pool</artifactId>
            <version>${activemq.version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.bjuvensjo</groupId>
            <artifactId>rsimulator-jms</artifactId>
            <version>${rsimulator.version}</version>
        </dependency>
    </project>