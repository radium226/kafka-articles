log4j.rootLogger=${LOG_LEVEL}, STDOUT

log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
log4j.appender.STDOUT.layout.ConversionPattern=[%d] %p %m (%c:%L)%n

log4j.logger.kafka=ERROR, STDOUT
log4j.logger.org.apache.zookeeper=ERROR, STDOUT
log4j.logger.org.apache.kafka=ERROR, STDOUT
log4j.logger.org.I0Itec.zkclient=ERROR, STDOUT
log4j.additivity.kafka.server=false
log4j.additivity.kafka.consumer.ZookeeperConsumerConnector=false