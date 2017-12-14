package scalaexchange
package app

import cats.effect.IO
import freestyle.rpc.client._
import io.grpc.ManagedChannel

trait Implicits extends scalaexchange.CommonImplicits {

  val channelFor: ManagedChannelFor = ManagedChannelForAddress("localhost", 8080)

  val channelConfigList: List[ManagedChannelConfig] = List(UsePlaintext(true))

  val managedChannelInterpreter =
    new ManagedChannelInterpreter[IO](channelFor, channelConfigList)

  val channel: ManagedChannel = managedChannelInterpreter.build(channelFor, channelConfigList)

}
