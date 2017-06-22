package io.estatico.generic

trait LabelledGProduct[A] extends GProduct[A]

object LabelledGProduct {

  @inline def to[A](a: A)(
    implicit ev: LabelledGProduct[A]
  ): A with ev.Repr = a.asInstanceOf[A with ev.Repr]

  type Aux[A, R] = LabelledGProduct[A] { type Repr = R }

  def apply[A](implicit ev: LabelledGProduct[A]): Aux[A, ev.Repr] = ev

  def instance[A, R]: Aux[A, R] = _instance.asInstanceOf[Aux[A, R]]

  private val _instance = new LabelledGProduct[Any] { type Repr = Any }
}
