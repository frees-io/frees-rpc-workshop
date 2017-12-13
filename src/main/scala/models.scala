package scalaexchange

sealed trait EventType
case object ProcessedCheckout   extends EventType
case object UnprocessedCheckout extends EventType
case object Login               extends EventType

case class UserEvent(userId: Int, eventType: EventType, date: String)
