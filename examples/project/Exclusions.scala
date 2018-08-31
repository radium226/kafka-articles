import sbt._

object Exclusions {

  lazy val slf4j = ExclusionRule(organization = "org.slf4j")

  lazy val zooKeeper = ExclusionRule(organization = "org.apache.zookeeper")

  lazy val kafka = ExclusionRule(organization = "org.apache.kafka")

  lazy val snappy = ExclusionRule(organization = "org.xerial.snappy")

  lazy val config = ExclusionRule(organization = "com.typesafe", name= "config")

  lazy val scalaModules = ExclusionRule(organization = "org.scala-lang.modules")

}