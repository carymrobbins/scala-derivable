package io.estatico.test

import io.estatico.generic._
import io.estatico.generic.IsGCons.ops._

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

  implicit val int: CsvEncoder[Int] = CsvEncoder.fromToString

  implicit val float: CsvEncoder[Float] = CsvEncoder.fromToString

  implicit val double: CsvEncoder[Double] = CsvEncoder.fromToString

  implicit val string: CsvEncoder[String] = CsvEncoder.instance(s =>
    if (s.contains(',')) '"' + s.replace("\"", "\"\"") + '"' else s
  )

  def gProduct[A, H, T <: GList](
    implicit
    gProd: GProduct.Aux[A, H #: T],
    isGCons: IsGCons.Aux[A, H, T],
    hEnc: CsvEncoder[H],
    tEnc: CsvEncoder[GList.Of[A, T]]
  ): CsvEncoder[A] = CsvEncoder.instance(a =>
    gCons[A, H, T].encode(GProduct.to[A](a))
  )

  implicit def gCons[A, H, T <: GList](
    implicit
    hEnc: CsvEncoder[H],
    tEnc: CsvEncoder[GList.Of[A, T]],
    isGCons: IsGCons.Aux[A, H, T]
  ): CsvEncoder[GList.Of[A, H #: T]] = CsvEncoder.instance { a =>
    hEnc.encode(a.head) + ',' + tEnc.encode(a.tail)
  }

  implicit def gSingle[A, H](
    implicit
    hEnc: CsvEncoder[H],
    isGCons: IsGCons.Aux[A, H, GNil]
  ): CsvEncoder[GList.Of[A, H #: GNil]] = CsvEncoder.instance { a =>
    hEnc.encode(a.head)
  }

  def derive[A](implicit ev: Derived[A]): CsvEncoder[A] = ev

  type Derived[A] = CsvEncoder[A] with DerivedTag
  trait DerivedTag
  object DerivedTag {

    /** For products with arity == 1 */
    implicit def derivedGSingle[A, H](
      implicit
      gProd: GProduct.Aux[A, H #: GNil],
      isGCons: IsGCons.Aux[A, H, GNil],
      hEnc: CsvEncoder[H]
    ): Derived[A] = cast(gSingle[A, H])

    /** For products with arity >= 2 */
    implicit def derivedGCons[A, H, T <: GList](
      implicit
      gProd: GProduct.Aux[A, H #: T],
      isGCons: IsGCons.Aux[A, H, T],
      hEnc: CsvEncoder[H],
      tEnc: CsvEncoder[GList.Of[A, T]]
    ): Derived[A] = cast(gProduct[A, H, T])


    def cast[A, AA <: A](x: CsvEncoder[AA]): Derived[A] = x.asInstanceOf[Derived[A]]
  }
}
