package common.environment

sealed trait CreateTopicStrategy {



}

object CreateTopicStrategy {

  case object AllowOnlyOneSchema extends CreateTopicStrategy

  case object AllowMultipleSchemas extends CreateTopicStrategy

}
