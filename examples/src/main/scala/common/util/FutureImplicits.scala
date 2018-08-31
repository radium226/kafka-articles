package common.util

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait FutureImplicits {

  implicit class FutureToTry[T](future: Future[T]) {

    def toTry()(implicit executionContext: ExecutionContext): Future[Try[T]] = {
      future
        .map(Success(_): Try[T])
        .recover({ case e =>
          Failure(e)
        })
    }

  }

}
