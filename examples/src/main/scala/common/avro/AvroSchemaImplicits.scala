package common.avro

import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecord

import scala.reflect.{ClassTag, classTag}

trait AvroSchemaImplicits {

  implicit def stringHasSchema = new HasAvroSchema[String] {

    override def avroSchema(): Schema = new Schema.Parser().parse("{\"type\": \"string\"}")

  }

  implicit def specificRecordHasAvroSchema[T <: SpecificRecord:ClassTag] = new HasAvroSchema[T] {

    override def avroSchema: Schema = {
      import scala.reflect.runtime.universe
      import scala.reflect.runtime.universe.TermName


      val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
      val runtimeClass = classTag[T].runtimeClass

      val moduleMirror = runtimeMirror.reflectModule(runtimeMirror.classSymbol(runtimeClass).companion.asModule)

      val instanceMirror = runtimeMirror.reflect(moduleMirror.instance)
      val fieldSymbold = moduleMirror.symbol.typeSignature.member(TermName("SCHEMA$")).asTerm

      instanceMirror.reflectField(fieldSymbold).get.asInstanceOf[Schema]
    }

  }

}
