package common.schemaregistry

import com.softwaremill.sttp._
import com.typesafe.config.Config
import common.avro.HasAvroSchema
import org.apache.avro.Schema
import common.util.Logging

import scala.util.{Failure, Success, Try}

// https://github.com/confluentinc/schema-registry
object SchemaRegistry extends Logging {

  private implicit val sttpBackend = HttpURLConnectionBackend()

  private val ContentType = "application/vnd.schemaregistry.v1+json"

  def updateSubjectCompatibility(subjectName: SubjectName, compatibility: Compatibility)(implicit config: Config): Try[Unit] = {
    val json = s"""{ "compatibility":"${compatibility}" }""""
    val request = sttp
      .put(uri"${config.getString("schema.registry.url")}/config/${subjectName}")
      .header("Content-Type", ContentType)
      .body(json)
    val response = request.send()
    response.body match {
      case Left(content) =>
        warn(content)
        Failure(new Exception(content))
      case Right(content) =>
        info(content)
        Success(())
    }
  }

  def registerSubject[T:HasAvroSchema](subjectName: SubjectName)(implicit config: Config): Try[Unit] = {
    val avroSchema = implicitly[HasAvroSchema[T]].avroSchema
    println(s" --> Registering ${subjectName} subject with ${avroSchema.getName} schema")
    SchemaRegistry.registerSubjectSchema(subjectName, avroSchema)
  }

  def registerSubjectSchema(subjectName: SubjectName, schema: Schema)(implicit config: Config): Try[Unit] = {
    val json = s"""{ "schema": "${schema.toString.replace(""""""", """\"""")}" }"""
    val request = sttp
      .post(uri"${config.getString("schema.registry.url")}/subjects/${subjectName}/versions")
      .header("Content-Type", ContentType)
      .body(json)
    val response = request.send()
    response.body match {
      case Left(content) =>
        warn(content)
        Failure(new Exception(content))
      case Right(content) =>
        info(content)
        Success(())
    }
  }

  def registeredSchemas(): Try[Unit] = {
    null
  }


}
