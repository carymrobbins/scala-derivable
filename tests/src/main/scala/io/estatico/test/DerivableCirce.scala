package io.estatico.test

import io.circe._
import io.estatico.generic._
import io.estatico.generic.IsGNel.ops._

object DerivableCirce {

  def encoder[A](implicit ev: DerivedObjectEncoder[A]): Encoder[A] = ev

  type DerivedObjectEncoder[A] = ObjectEncoder[A] with DerivedTag
  trait DerivedTag
  object DerivedTag {
    implicit def derived[A, L <: GList](
      implicit
      gProd: LabelledGProduct.Aux[A, L],
      lEnc: ObjectEncoder[GList.Of[A, L]]
    ): DerivedObjectEncoder[A] = gProductEnc[A, L].asInstanceOf[DerivedObjectEncoder[A]]
  }

  def gProductEnc[A, L <: GList](
    implicit
    gProd: LabelledGProduct.Aux[A, L],
    lEnc: ObjectEncoder[GList.Of[A, L]]
  ): ObjectEncoder[A] = ObjectEncoder.instance(a =>
    ObjectEncoder[GList.Of[A, L]].encodeObject(LabelledGProduct.to[A](a))
  )

  implicit def gNelEnc[A, K <: String, H, T <: GList](
    implicit
    hEnc: Encoder[H],
    tEnc: ObjectEncoder[GList.Of[A, T]],
    isGNel: IsGNel.Aux[A, GField[K, H], T],
    k: Witness.Aux[K]
  ): ObjectEncoder[GList.Of[A, GField[K, H] #: T]] = ObjectEncoder(a =>
    (k.value, hEnc.apply(a.head)) +: tEnc.encodeObject(a.tail)
  )

  implicit def gNilEnc[A](
    implicit gProd: LabelledGProduct[A]
  ): ObjectEncoder[GList.Of[A, GNil]] = ObjectEncoder.instance(_ => JsonObject.empty)
}


