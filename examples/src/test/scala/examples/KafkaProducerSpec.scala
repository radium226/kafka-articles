package examples

import common.kafka._
import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}
import common.environment.{Environment, Topic}
import common.kafka.Kafka
import event.{Events, Login, Logout}
import org.scalatest.FlatSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import common.Implicits._
import common.schemaregistry.SchemaRegistry

import scala.language.postfixOps
import scala.util.Failure

class KafkaProducerSpec extends ExampleSpec {

  object TopicNames {

    val LoginEvents = "login-events"
    val LogoutEvents = "logout-events"

  }

  import TopicNames._

  override def beforeAll(): Unit = {
    super.beforeAll()


    // https://www.confluent.io/blog/put-several-event-types-kafka-topic/
    (for {
      _ <- SchemaRegistry.registerSubject[String](s"${LoginEvents}-key")
      _ <- SchemaRegistry.registerSubject[Login](Login.SCHEMA$.getFullName)
      _ <- SchemaRegistry.registerSubject[String](s"${LogoutEvents}-key")
      _ <- SchemaRegistry.registerSubject[Logout](Logout.SCHEMA$.getFullName)

      _ <- Kafka.createTopicIfNotExists(LoginEvents, partitionCount = 5)
      _ <- Kafka.createTopicIfNotExists(LogoutEvents, partitionCount = 5)
    } yield ()) match {
      case Failure(e) => throw e
      case _ =>
    }
  }

  "Schema Registry" should "be able to list registered schemas" in {
    //SchemaRegistry.
  }

  "We" should "not be able to put another kind of event in the topic" in {

    val graph = Source.single(Logout("toto", 12345))
        .map({ logout => (logout.customerId, logout) })
        .toMat(Topic.sink[String, Logout](LoginEvents))(Keep.right)

    an [Exception] should be thrownBy waitFor(graph.run())
  }


  "We" should "be able to put some events into a Kafka topic" in {
    logger.info(s"Putting some events into the ${LoginEvents} topic")
    val putGraph = Events.loginSource()
        .map({ loginEvent =>
          (loginEvent.customerId, loginEvent)
        })
        .toMat(Topic.sink[String, Login](LoginEvents))(Keep.both)

    val (cancelable, futureDone) = putGraph.run()
    sleep(10 seconds)
    cancelable.cancel()
    waitFor(futureDone)

    logger.info(s"Retreiving events from the ${LoginEvents} topic")
    val source = Topic.sourceWithMultipleSchema[String, Symbol](LoginEvents, Offset.Earliest) {
      case Login(customerId, epoch) => 'login
      case Logout(customerId, epoch) => 'logout
    }

    val retreiveGraph = source
        .mapAsync(1)({ case (_, event) =>
          //committer.commit().map({ _ => event })
          Future.successful(event)
        })
        .toMat(Sink.seq)(Keep.both)
    val (control, futureLogins) = retreiveGraph.run()
    sleep(25 seconds)
    control.stop()

    val logins = waitFor(futureLogins)
    println(logins)
  }

}
