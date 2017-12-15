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

    val (segments: IO[SegmentList], stream: Observable[UserEvent], ack: IO[Ack]) =
      (
        rfmClient.segments(Empty),
        rfmClient.userEvents(Empty),
        rfmClient.orderStream(ordersStreamObs)
      )

    println(s"Segments: \n${segments.unsafeRunSync().list.mkString("\n")}\n")
    println(s"Client Streaming: \n${ack.unsafeRunSync()}\n")
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

  private[this] def ordersStreamObs: Observable[Order] = {
    val orderList: List[Order] = (1 to 1000).map { customerId =>
      import com.fortysevendeg.scalacheck.datetime.GenDateTime
      import org.joda.time.{DateTime, Period}
      import org.scalacheck._
      import com.fortysevendeg.scalacheck.datetime.instances.joda.jodaForPeriod

      (for {
        date    <- GenDateTime.genDateTimeWithinRange(DateTime.parse("2017-12-01"), Period.days(22))
        orderId <- Gen.uuid
        total   <- Gen.choose[Int](5, 200)
      } yield
        Order(
          customerId,
          CustomerData(date.toString, orderId.toString, total)
        )).sample.get
    }.toList

    Observable.fromIterable(orderList)
  }

}
