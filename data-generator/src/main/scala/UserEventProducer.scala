package scalaexchange

import com.fortysevendeg.scalacheck.datetime.GenDateTime
import com.fortysevendeg.scalacheck.datetime.instances.joda.jodaForPeriod
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.observers.Subscriber
import org.joda.time.{DateTime, Period}

import scala.concurrent.duration.FiniteDuration

/**
  * This producer, based on scalacheck generators will returns `UserEvent` with
  * the following potential values:
  *
  * Id: a value between 1 to 100.
  * EventType: ProcessedCheckout, UnprocessedCheckout or Login.
  * Date: dates between 2017-12-01 - 2017-12-16.
  *
  * @param interval Interval of time the producer will send a new message.
  */
class UserEventProducer(interval: FiniteDuration) extends Observable[UserEvent] {

  override def unsafeSubscribeFn(subscriber: Subscriber[UserEvent]): Cancelable = {

    val userEventRandom: Observable[UserEvent] =
      Observable
        .fromStateAction(eventsGen)(Nil)
        .flatMap { a =>
          Observable.now(a).delaySubscription(interval)
        }

    userEventRandom.drop(1).unsafeSubscribeFn(subscriber)
  }

  private[this] def eventsGen(initialState: List[UserEvent]): (UserEvent, List[UserEvent]) = {

    import org.scalacheck._

    val dataGen: Arbitrary[UserEvent] = Arbitrary {
      import Gen._
      for {
        id        <- choose(1, 100)
        eventType <- Gen.oneOf(List(ProcessedCheckout, UnprocessedCheckout, Login))
        date      <- GenDateTime.genDateTimeWithinRange(DateTime.parse("2017-12-01"), Period.days(16))
      } yield UserEvent(id, eventType, date.toString())
    }

    val newEvent: UserEvent = dataGen.arbitrary.sample
      .getOrElse(throw DataGenerationException("Exception creating new random event"))

    (newEvent, initialState :+ newEvent)
  }
}
