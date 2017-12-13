package scalaexchange

case class DataGenerationException(message: String, maybeCause: Option[Throwable] = None)
  extends RuntimeException(message) {

  maybeCause foreach initCause
}