# Sonatype deploy

## Snapshot

With SNAPSHOT versions:

    mvn -DOSSRH_USERNAME=<username> -DOSSRH_PASSWORD=<password> -s settings.xml clean install deploy 

## Release

With fixed versions:

    mvn -DOSSRH_USERNAME=<username> -DOSSRH_PASSWORD=<password> -s settings.xml -Dmaven.javadoc.skip=false -Dmaven.javadoc.failOnError=false clean install deploy