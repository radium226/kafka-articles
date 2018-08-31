package event

import java.time.Instant

import akka.actor.Cancellable
import akka.stream.scaladsl.Source

import scala.concurrent.duration._

import scala.language.postfixOps


object Events {

  private def source[Event](createEvent: () => Event): Source[Event, Cancellable] = {
    Source.tick(0 second, 1 second, ())
        .map({ _ => createEvent() })
  }

  def loginSource() = source[Login]({ () =>
    Login("customerId", Instant.now().toEpochMilli)
  })

  def logoutSource() = source[Logout]({ () =>
    Logout("customerId", Instant.now().toEpochMilli)
  })

}
