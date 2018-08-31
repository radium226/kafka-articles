package common.environment

import akka.Done
import akka.kafka.scaladsl.Consumer.Control
import akka.stream._
import akka.stream.scaladsl._
import common.Implicits._
import common.kafka.{Committer, Kafka, Offset, TopicName}
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer, KafkaAvroSerializerConfig}

import scala.concurrent.Future
import scala.reflect.{ClassTag, classTag}

object Topic {

  def sink[Key, Value](topicName: TopicName)(implicit actorMaterializer: ActorMaterializer): Sink[(Key, Value), Future[Done]] = {
    val serializerConfig = actorMaterializer.system.settings.config.getConfig("akka.kafka.producer.kafka-avro-serializer")
    val keySerializer = new KafkaAvroSerializer()
    keySerializer.configure(serializerConfig, true)

    val valueSerializer = new KafkaAvroSerializer()
    valueSerializer.configure(serializerConfig, false)

    Flow[(Key, Value)]
        .map({ case (key, value) =>
          (key.asInstanceOf[AnyRef], value.asInstanceOf[AnyRef])
        })
        .toMat(Kafka.sink[AnyRef, AnyRef](topicName, keySerializer, valueSerializer))(Keep.right)
  }

  def sourceWithSingleSchema[Key:ClassTag, Value:ClassTag](topicName: TopicName, offset: Offset = Offset.Latest)(implicit actorMaterializer: ActorMaterializer): Source[(Key, Value), Control] = {
    sourceWithMultipleSchema[Key, Value](topicName, offset) {
      case value if classTag[Value].runtimeClass.isInstance(value) => value.asInstanceOf[Value]
    }
  }

  def sourceWithMultipleSchema[Key:ClassTag, Value:ClassTag](topicName: TopicName, offset: Offset = Offset.Latest)(transform: PartialFunction[AnyRef, Value])(implicit actorMaterializer: ActorMaterializer): Source[(Key, Value), Control] = {
    val deserializerConfig = actorMaterializer.system.settings.config.getConfig("akka.kafka.consumer.kafka-avro-deserializer")

    val keyDeserializer = new KafkaAvroDeserializer()
    keyDeserializer.configure(deserializerConfig, true)

    val valueDeserializer = new KafkaAvroDeserializer()
    valueDeserializer.configure(deserializerConfig, false)

    Kafka.source[AnyRef, AnyRef](topicName, keyDeserializer, valueDeserializer, offset)
        .map({ case (key, value) => (key.asInstanceOf[Key], value) })
        .collect(new PartialFunction[(Key, AnyRef), (Key, Value)] {

          override def isDefinedAt(keyAndValueAsAnyRef: (Key, AnyRef)): Boolean = {
            val (key: Key, valueAsAnyRef: AnyRef) = keyAndValueAsAnyRef
            transform.isDefinedAt(valueAsAnyRef)
          }

          override def apply(keyAndValueAsAnyRef: (Key, AnyRef)): (Key, Value) = {
            val (key: Key, valueAsAnyRef: AnyRef) = keyAndValueAsAnyRef
            (key, transform.apply(valueAsAnyRef))
          }

        })

  }

  def committableSourceWithMultipleSchema[Key:ClassTag, Value:ClassTag](topicName: TopicName, offset: Offset = Offset.Latest)(transform: PartialFunction[AnyRef, Value])(implicit actorMaterializer: ActorMaterializer): Source[(Key, Value, Committer), Control] = {
    val deserializerConfig = actorMaterializer.system.settings.config.getConfig("akka.kafka.consumer.kafka-avro-deserializer")

    val keyDeserializer = new KafkaAvroDeserializer()
    keyDeserializer.configure(deserializerConfig, true)

    val valueDeserializer = new KafkaAvroDeserializer()
    valueDeserializer.configure(deserializerConfig, false)

    Kafka.committableSource[AnyRef, AnyRef](topicName, keyDeserializer, valueDeserializer, offset)
        .map({ case (key, value, committer) => (key.asInstanceOf[Key], value, committer) })
        .collect(new PartialFunction[(Key, AnyRef, Committer), (Key, Value, Committer)] {

          override def isDefinedAt(keyAndValueAsAnyRefAndCommitter: (Key, AnyRef, Committer)): Boolean = {
            val (_, valueAsAnyRef, _) = keyAndValueAsAnyRefAndCommitter
            transform.isDefinedAt(valueAsAnyRef)
          }

          override def apply(keyAndValueAsAnyRefAndCommitter: (Key, AnyRef, Committer)): (Key, Value, Committer) = {
            val (key, valueAsAnyRef, committer) = keyAndValueAsAnyRefAndCommitter
            (key, transform.apply(valueAsAnyRef), committer)
          }

        })

  }

}
