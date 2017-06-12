package io.estatico.test

import io.estatico.generic._

trait CsvEncoder[A] {
  def encode(a: A): String
}

object CsvEncoder {

  def apply[A](implicit ev: CsvEncoder[A]): CsvEncoder[A] = ev

  def instance[A](f: A => String): CsvEncoder[A] = new CsvEncoder[A] {
    override def encode(a: A): String = f(a)
  }

  def fromToString[A]: CsvEncoder[A] = _fromToString.asInstanceOf[CsvEncoder[A]]

  // Cached instance for all fromToString instances.
  private val _fromToString = CsvEncoder.instance[Any](_.toString)

  def derive[C]: CsvEncoderDeriver[C] = new CsvEncoderDeriver[C]

  implicit val int: CsvEncoder[Int] = CsvEncoder.fromToString

  implicit val float: CsvEncoder[Float] = CsvEncoder.fromToString

  implicit val string: CsvEncoder[String] = CsvEncoder.instance(s =>
    if (s.contains(',')) '"' + s.replace("\"", "\"\"") + '"' else s
  )

  implicit def gFields1[A](implicit aEnc: CsvEncoder[A]): CsvEncoder[GenericFields._1[A]]
    = CsvEncoder.instance(a => aEnc.encode(GenericList.head(a)))
}

final class CsvEncoderDeriver[C]
object CsvEncoderDeriver {
  implicit def derive[C, H, T](c: CsvEncoderDeriver[C])(
    implicit
    gl: GenericList[C, H, T],
    hEnc: CsvEncoder[H],
    tEnc: CsvEncoder[T]
  ): CsvEncoder[C] = CsvEncoder.instance(a =>
    hEnc.encode(gl.head(a)) + ',' + tEnc.encode(gl.tail(a))
  )
}
