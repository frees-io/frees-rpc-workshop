package scalaexchange
package serverapp

import cats.{~>, Applicative}
import freestyle.rpc.server.handlers.GrpcServerHandler
import freestyle.rpc.server._
import freestyle.rpc.server.implicits._
import scalaexchange.services.protocol.RFMAnalysisService

import scalaexchange.services.runtime.RFMAnalysisServiceHandler

trait Implicits extends scalaexchange.CommonImplicits {

  implicit def rfmAnalisysServiceHandler[F[_]: Applicative]: RFMAnalysisServiceHandler[F] =
    new RFMAnalysisServiceHandler[F]

  val grpcConfigs: List[GrpcConfig] = List(
    AddService(
      RFMAnalysisService.bindService[ConcurrentMonad]
    )
  )

  implicit def grpcServerHandler: GrpcServer.Op ~> ConcurrentMonad =
    new GrpcServerHandler[ConcurrentMonad] andThen
      new GrpcKInterpreter[ConcurrentMonad](ServerW(port, grpcConfigs).server)

}
