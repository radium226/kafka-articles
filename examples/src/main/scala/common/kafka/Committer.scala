package common.kafka

import akka.Done

import scala.concurrent.Future

trait Committer {

  def commit(): Future[Done]

}
