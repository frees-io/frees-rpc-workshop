package scalaexchange
package services

import freestyle.rpc.protocol._
import monix.reactive.Observable

object protocol {

  final case class Segment(
      title: String,
      minRecency: Int,
      maxRecency: Int,
      minFrequency: Int,
      maxFrequency: Int,
      minMonetary: Int,
      maxMonetary: Int
  )

  final case class SegmentList(list: List[Segment])

  @service
  trait RFMAnalysisService[F[_]] {

    @rpc(Avro) def segments(empty: Empty.type): F[SegmentList]

    @rpc(Avro)
    @stream[ResponseStreaming.type]
    def userEvents(empty: Empty.type): F[Observable[UserEvent]]

  }

}
