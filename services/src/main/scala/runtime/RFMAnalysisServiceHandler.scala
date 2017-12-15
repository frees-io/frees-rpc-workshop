package scalaexchange
package services
package runtime

import java.io.File

import cats.{~>, Applicative}
import com.github.tototoshi.csv._
import freestyle.rpc.protocol._
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

import scalaexchange.datagenerator.StreamingService
import scalaexchange.services.protocol._

class RFMAnalysisServiceHandler[F[_]: Applicative](implicit S: Scheduler, T2F: Task ~> F)
    extends RFMAnalysisService[F] {

  private[this] val segmentList: List[Segment] = List(
    Segment("Champions", 4, 5, 4, 5, 4, 5),
    Segment("Loyal Customers", 2, 5, 3, 5, 3, 5),
    Segment("Potential Loyalist", 3, 5, 1, 3, 1, 3),
    Segment("New Customers", 4, 5, 0, 1, 0, 1),
    Segment("Customers Needing Attention", 3, 4, 0, 1, 0, 1),
    Segment("About To Sleep", 2, 3, 0, 2, 0, 2),
    Segment("Can't Lose Them", 0, 1, 4, 5, 4, 5),
    Segment("At Risk", 0, 2, 2, 5, 2, 5),
    Segment("Hibernating", 1, 2, 1, 2, 1, 2),
    Segment("Lost", 0, 2, 0, 2, 0, 2)
  )

  private[this] val streamingService = new StreamingService

  private[this] val outPath: String = "orders.csv"

  override def segments(empty: Empty.type): F[protocol.SegmentList] =
    Applicative[F].pure(SegmentList(segmentList))

  override def userEvents(empty: Empty.type): F[Observable[UserEvent]] =
    Applicative[F].pure(streamingService.userEventsStream)

  override def orderStream(orders: Observable[Order]): F[Ack] = T2F {
    val f: File           = new File(outPath)
    val writer: CSVWriter = CSVWriter.open(f)

    orders
      .foreachL { order =>
        writer.writeRow(
          List(order.data.date, order.data.orderId, order.customerId, order.data.total)
        )
      }
      .map { _ =>
        writer.close()
        Ack(" 👍 ")
      }
  }
}
