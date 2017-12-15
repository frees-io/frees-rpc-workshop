package scalaexchange
package app

import cats.effect.IO
import freestyle.rpc.protocol.Empty
import freestyle.asyncCatsEffect.implicits._
import freestyle.rpc.client.implicits._
import monix.reactive.Observable

import scala.concurrent.Await
import scala.concurrent.duration._
import scalaexchange.services.protocol._

object AppRFMClient extends Implicits {

  def main(args: Array[String]): Unit = {

    implicit val rfmClient: RFMAnalysisService.Client[IO] =
      RFMAnalysisService.client[IO](channel)

    val (segments: IO[SegmentList], stream: Observable[UserEvent]) =
      (rfmClient.segments(Empty), rfmClient.userEvents(Empty))

    println(s"Segments: \n${segments.unsafeRunSync().list.mkString("\n")}\n")
    Await.ready(
      stream
        .map { u =>
          println(u)
          u
        }
        .completedL
        .runAsync,
      Duration.Inf)
  }

}
