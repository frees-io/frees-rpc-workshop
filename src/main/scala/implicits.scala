package scalaexchange

import monix.execution.Scheduler

trait CommonImplicits {

  implicit val S: Scheduler = monix.execution.Scheduler.Implicits.global

}

object implicits extends CommonImplicits
