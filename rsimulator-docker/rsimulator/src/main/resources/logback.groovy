import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.INFO

def logPpattern = "%d{HH:mm:ss.SSS} %-6relative %-10.8thread %-5level %-10logger{0} %msg%n"

def rsimulator_home = System.getenv().get("RSIMULATOR_HOME")
appender("FILE", FileAppender) {
    file = "${rsimulator_home}/rsimulator.log"
    immediateFlush = true
    encoder(PatternLayoutEncoder) {
        pattern = logPpattern
    }
    append = false
}
root(INFO, ["FILE"])
