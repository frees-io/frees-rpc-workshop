package scalaexchange
package serverapp

import freestyle._
import freestyle.rpc.server._
import freestyle.rpc.server.implicits._

object ServerApp extends scalaexchange.serverapp.Implicits {

  def main(args: Array[String]): Unit =
    server[GrpcServerApp.Op].interpret[ConcurrentMonad].unsafeRunSync()

}
