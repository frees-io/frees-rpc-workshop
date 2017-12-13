package scalaexchange

import cats.Monad
import freestyle._

object algebra {

  @free sealed trait AlgebraM {
    def hello: FS[String]
  }

  trait Implicits {
    implicit def algebraMHandler[M[_]](implicit M: Monad[M]): AlgebraM.Handler[M] =
      new AlgebraM.Handler[M] {
        def hello: M[String] = M.pure("Hello World!")
      }

  }

  object implicits extends Implicits
}
