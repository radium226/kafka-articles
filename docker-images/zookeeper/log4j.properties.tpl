# Define some default values that can be overridden by system properties
log4j.rootLogger=WARN, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ISO8601} [myid:%X{myid}] - %-5p [%t:%C{1}@%L] - %m%n

log4j.logger.zookeeper=${LOG_LEVEL}
log4j.logger.org.apache.zookeeper=${LOG_LEVEL}