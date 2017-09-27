FROM openjdk:8-jdk

# update dpkg repositories
RUN apt-get update 

# install wget
RUN apt-get install -y wget

# get jetty
RUN wget --no-verbose -O /tmp/jetty-distribution-9.4.7.v20170914.tar.gz http://central.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.7.v20170914/jetty-distribution-9.4.7.v20170914.tar.gz

# install jetty
RUN tar xzf /tmp/jetty-distribution-9.4.7.v20170914.tar.gz -C /opt/
RUN rm -f /tmp/jetty-distribution-9.4.7.v20170914.tar.gz
ENV JETTY_HOME /opt/jetty-distribution-9.4.7.v20170914

ENV RSIMULATOR_HOME /var/rsimulator_home

# Rsimulator home directory is a volume, so it can be persisted and survive image upgrades
VOLUME /var/rsimulator_home

# for main web interface:
EXPOSE 8080

COPY rsimulator/target/rsimulator $JETTY_HOME/webapps/rsimulator

ENTRYPOINT ["sh", "-c", "cd $JETTY_HOME; java -DrootPath=$RSIMULATOR_HOME -DuseRootRelativePath=true -jar start.jar"]