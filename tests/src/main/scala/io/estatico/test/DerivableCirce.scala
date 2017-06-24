package io.estatico.test

import io.circe._

object DerivableCirce {

  import io.estatico.generic._

  def deriveEncoder[A](implicit ev: DerivedObjectEncoder[A]): Encoder[A] = ev

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
  ): ObjectEncoder[A] = lEnc.asInstanceOf[ObjectEncoder[A]]

  implicit def gConsEnc[A, H, T <: GList](
    implicit
    hEnc: Encoder[H],
    tEnc: ObjectEncoder[GList.Of[A, T]],
    isGCons: IsGCons.Labelled.Aux[A, H, T]
  ): ObjectEncoder[GList.Of[A, H #: T]] = ObjectEncoder.instance(a =>
    (isGCons.headLabel, hEnc.apply(isGCons.head(a))) +: tEnc.encodeObject(isGCons.tail(a))
  )

  implicit def gNilEnc[A](
    implicit gProd: LabelledGProduct[A]
  ): ObjectEncoder[GList.Of[A, GNil]] = ObjectEncoder.instance(_ => JsonObject.empty)
}

object ShapelessCirce {

  import shapeless._
  import labelled._

  def deriveEncoder[A](implicit ev: DerivedObjectEncoder[A]): Encoder[A] = ev

  type DerivedObjectEncoder[A] = ObjectEncoder[A] with DerivedTag
  trait DerivedTag
  object DerivedTag {
    implicit def derived[A, L <: HList](
      implicit
      g: LabelledGeneric.Aux[A, L],
      lEnc: ObjectEncoder[L]
    ): DerivedObjectEncoder[A] = genericEnc[A, L].asInstanceOf[DerivedObjectEncoder[A]]
  }

  def genericEnc[A, L <: HList](
    implicit
    g: LabelledGeneric.Aux[A, L],
    lEnc: ObjectEncoder[L]
  ): ObjectEncoder[A] = ObjectEncoder.instance(a => lEnc.encodeObject(g.to(a)))

  implicit def gConsEnc[K <: Symbol, H, T <: HList](
    implicit
    w: Witness.Aux[K],
    hEnc: Encoder[H],
    tEnc: ObjectEncoder[T]
  ): ObjectEncoder[FieldType[K, H] :: T] = ObjectEncoder.instance { case h :: t =>
    (w.value.name, hEnc(h)) +: tEnc.encodeObject(t)
  }

  implicit val gNilEnc: ObjectEncoder[HNil] = ObjectEncoder.instance(_ => JsonObject.empty)
}
