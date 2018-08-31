package common

import common.avro.AvroSchemaImplicits
import common.util.{FutureImplicits, TypesafeConfigImplicits}

object Implicits extends AvroSchemaImplicits with TypesafeConfigImplicits with FutureImplicits
