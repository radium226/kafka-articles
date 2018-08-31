import sbt._

object Dependencies {

  lazy val sttp = Seq(
    "com.softwaremill.sttp" %% "core" % "1.3.0"
  )

  lazy val scala = Seq(
    "org.scala-lang" % "scala-reflect" % "2.12.6"
  )

  lazy val snappy = Seq("org.xerial.snappy" % "snappy-java" % Versions.snappy)

  lazy val zooKeeper = Seq("org.apache.zookeeper" % "zookeeper" % Versions.zooKeeper)
      .map(_.excludeAll(Exclusions.zooKeeper, Exclusions.slf4j))

  lazy val zkClient = Seq("com.101tec" % "zkclient" % Versions.zkClient)
      .map(_.excludeAll(Exclusions.zooKeeper, Exclusions.slf4j))

  lazy val kafkaAvroSerializer = Seq("io.confluent" % "kafka-avro-serializer" % Versions.kafkaAvroSerializer)
      .map(_.excludeAll(Exclusions.snappy, Exclusions.slf4j))

  lazy val logback = Seq("ch.qos.logback" % "logback-classic" % Versions.logback)

  lazy val kafka = Seq("org.apache.kafka" % "kafka-clients" % Versions.kafka)
      .map(_.excludeAll(Exclusions.zooKeeper, Exclusions.snappy, Exclusions.slf4j))

  lazy val config = Seq("com.typesafe" % "config" % Versions.config)

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-stream" % Versions.akkaStream,
    "com.typesafe.akka" %% "akka-stream-kafka" % Versions.akkaStreamKafka
  ).map(_.excludeAll(Exclusions.kafka, Exclusions.slf4j, Exclusions.config))

  lazy val all = zkClient ++ kafkaAvroSerializer ++ logback ++ kafka ++ akka ++ zooKeeper ++ snappy ++ config ++ avro ++ scala ++ sttp

  lazy val avro = Seq("org.apache.avro" % "avro" % "1.8.2")

}