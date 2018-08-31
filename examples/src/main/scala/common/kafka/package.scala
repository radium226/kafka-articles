package common

package object kafka {

  type TopicName = String

  type Offset = String

  object Offset {

    val Earliest: Offset = "earliest"

    val Latest: Offset = "latest"

  }

}
