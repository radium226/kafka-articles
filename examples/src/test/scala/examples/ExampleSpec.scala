package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import common.util.Logging
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

abstract class ExampleSpec extends FlatSpec with BeforeAndAfterAll with Matchers with Logging {

  implicit var config: Config = _

  implicit var actorSystem: ActorSystem = _
  implicit var actorMaterializer: ActorMaterializer = _

  implicit var executionContext: ExecutionContext = _

  def sleep(duration: Duration): Unit = {
    Thread.sleep(duration.toMillis)
  }

  def waitFor[T](future: Future[T]): T = {
    Await.result(future, 1 minute) // FIXME: Put in config
  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    logger.info("Starting ActorSystem and ActorMaterializer")
    config = ConfigFactory.load()

    actorSystem = ActorSystem()
    actorMaterializer = ActorMaterializer()

    executionContext = actorSystem.dispatcher

  }

  override def afterAll(): Unit = {
    logger.info("Stopping ActorSystem and ActorMaterializer")
    actorMaterializer.shutdown()
    actorSystem.terminate()
    super.afterAll()
  }

}
