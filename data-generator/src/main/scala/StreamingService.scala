package scalaexchange

import monix.reactive.Observable

import scala.concurrent.duration._

class StreamingService {

  def userEventsStream: Observable[UserEvent] = {
    new UserEventProducer(500.milliseconds)
      .delayOnComplete(1.minute)
      .map { userEvent =>
        println(s"* New Event 👍  --> $userEvent")
        userEvent
      }
  }

}
