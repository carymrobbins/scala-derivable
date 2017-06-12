package io.estatico.test

trait CsvDecoder[A] {
  def decode(s: String): CsvDecoder.Result[A]
}

object CsvDecoder {

  def apply[A](implicit ev: CsvDecoder[A]): CsvDecoder[A] = ev

  def instance[A](f: String => Result[A]): CsvDecoder[A] = new CsvDecoder[A] {
    override def decode(s: String): Result[A] = f(s)
  }

  trait Result[+A]
  final case class Success[+A](a: A) extends Result[A]
  final case class Failure(message: String) extends Result[Nothing]
}
