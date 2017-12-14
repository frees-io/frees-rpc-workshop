package scalaexchange
package app

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scalaexchange.datagenerator.StreamingService

object AppStreamingService extends CommonImplicits {

  def main(args: Array[String]): Unit = {
    val streamingService: StreamingService = new StreamingService

    Await.ready(streamingService.userEventsStream.completedL.runAsync, Duration.Inf)
  }

}
