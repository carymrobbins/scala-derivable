package io.estatico.test

import io.estatico.generic._

trait CsvEncoder[A] {
  def encode(a: A): String
}

object CsvEncoder {

  def apply[A](implicit ev: CsvEncoder[A]): CsvEncoder[A] = ev

  def encode[A](a: A)(implicit ev: CsvEncoder[A]): String = ev.encode(a)

  def instance[A](f: A => String): CsvEncoder[A] = new CsvEncoder[A] {
    override def encode(a: A): String = f(a)
  }

  def fromToString[A]: CsvEncoder[A] = _fromToString.asInstanceOf[CsvEncoder[A]]

  // Cached instance for all fromToString instances.
  private val _fromToString = CsvEncoder.instance[Any](_.toString)

  def derive[A]: CsvEncoderDeriver[A] = new CsvEncoderDeriver[A]

  implicit val int: CsvEncoder[Int] = CsvEncoder.fromToString

  implicit val float: CsvEncoder[Float] = CsvEncoder.fromToString

  implicit val double: CsvEncoder[Double] = CsvEncoder.fromToString

  implicit val string: CsvEncoder[String] = CsvEncoder.instance(s =>
    if (s.contains(',')) '"' + s.replace("\"", "\"\"") + '"' else s
  )

  def gProduct[A, H, T <: GList](
    implicit
    gProd: GProduct.Aux[A, H #: T],
    isGNel: IsGNel.Aux[A, H, T],
    hEnc: CsvEncoder[H],
    tEnc: CsvEncoder[GList.Of[A, T]]
  ): CsvEncoder[A] = CsvEncoder.instance(a =>
    gNel[A, H, T].encode(GProduct.to[A](a))
  )

  implicit def gNel[A, H, T <: GList](
    implicit
    hEnc: CsvEncoder[H],
    tEnc: CsvEncoder[GList.Of[A, T]],
    isGNel: IsGNel.Aux[A, H, T]
  ): CsvEncoder[GList.Of[A, H #: T]] = CsvEncoder.instance { a =>
    //TODO: See if this will work
    //import IsGNel.ops._
    //hEnc.encode(nel.head) + ',' + tEnc.encode(nel.tail)
    hEnc.encode(isGNel.head(a)) + ',' + tEnc.encode(isGNel.tail(a))
  }

  implicit def gSingle[A, H](
    implicit
    hEnc: CsvEncoder[H],
    isGNel: IsGNel.Aux[A, H, GNil]
  ): CsvEncoder[GList.Of[A, H #: GNil]] = CsvEncoder.instance { nel =>
    hEnc.encode(isGNel.head(nel))
  }
}

final class CsvEncoderDeriver[A] {
  def apply[H, T <: GList](
    implicit
    gProd: GProduct.Aux[A, H #: T],
    isGNel: IsGNel.Aux[A, H, T],
    hEnc: CsvEncoder[H],
    tEnc: CsvEncoder[GList.Of[A, T]]
  ): CsvEncoder[A] = CsvEncoder.gProduct[A, H, T]
}
