package common.environment

import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import common.avro.HasAvroSchema
import common.kafka.{Kafka, TopicName}
import common.schemaregistry.SchemaRegistry
import common.util.Logging

import scala.util.Try

object Environment extends Logging {

  def createTopic[Key:HasAvroSchema, Value:HasAvroSchema](topicName: TopicName)(implicit actorMaterializer: ActorMaterializer, config: Config): Try[Unit] = {
    println(s"Creating the ${topicName} topic")
    for {
      _ <- Kafka.createTopicIfNotExists(topicName)
      _ = println(s"Registering the key")
      _ <- SchemaRegistry.registerSubjectSchema(s"${topicName}-key", implicitly[HasAvroSchema[Key]].avroSchema)
      _ =println(s"Registering the value")
      _ <- SchemaRegistry.registerSubjectSchema(s"${topicName}-value", implicitly[HasAvroSchema[Value]].avroSchema)
    } yield ()
  }

}

