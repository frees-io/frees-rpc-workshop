package scalaexchange

import cats.effect.IO

package object serverapp {

  type ConcurrentMonad[A] = IO[A]

  val port: Int = 8080

}
