# Sonatype deploy

## Snapshot

With SNAPSHOT versions:

    mvn clean install deploy 

## Release

With fixed versions:

    mvn -Dmaven.javadoc.skip=false -Dmaven.javadoc.failOnError=false clean install deploy