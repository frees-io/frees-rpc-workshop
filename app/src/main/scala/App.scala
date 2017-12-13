package scalaexchange

import monix.execution.Scheduler

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object App {

  implicit val S: Scheduler = monix.execution.Scheduler.Implicits.global

  def main(args: Array[String]): Unit = {
    val streamingService: StreamingService = new StreamingService

    Await.ready(streamingService.userEventsStream.completedL.runAsync, Duration.Inf)
  }

}
