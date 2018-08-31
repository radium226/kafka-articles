package common.util

import org.slf4j.{Logger, LoggerFactory}

trait Logging {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def info(message: String): Unit = logger.info(message)

  def warn(message: String): Unit = logger.warn(message)

  def error(message: String, throwable: Throwable) = logger.error(message, throwable)

}
