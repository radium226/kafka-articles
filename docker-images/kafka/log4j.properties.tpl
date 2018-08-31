log4j.rootLogger=WARN, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%d] %p %m (%c)%n

# Change the two lines below to adjust ZK client logging
log4j.logger.org.I0Itec.zkclient.ZkClient=${LOG_LEVEL}
log4j.logger.org.apache.zookeeper=${LOG_LEVEL}

# Change the two lines below to adjust the general broker logging level (output to server.log and stdout)
log4j.logger.kafka=${LOG_LEVEL}
log4j.logger.org.apache.kafka=${LOG_LEVEL}