package io.estatico.generic

trait GProduct[A] { type Repr }

object GProduct {

  @inline def to[A](a: A)(implicit ev: GProduct[A]): A with ev.Repr = a.asInstanceOf[A with ev.Repr]

  type Aux[A, R] = GProduct[A] { type Repr = R }

  def apply[A](implicit ev: GProduct[A]): Aux[A, ev.Repr] = ev

  def instance[A, R]: Aux[A, R] = _instance.asInstanceOf[Aux[A, R]]

  private val _instance = new GProduct[Any] { type Repr = Any }
}

sealed trait GList

object GList {
  type Of[A, L <: GList] = A with L
}

sealed trait #:[H, T <: GList] extends GList

sealed trait GNil extends GList

trait IsGNel[A] {
  type Head
  type Tail <: GList
  def head(a: GList.Of[A, Head #: Tail]): Head
  def tail(a: GList.Of[A, Head #: Tail]): GList.Of[A, Tail]
}

object IsGNel {

  type Aux[A, H, T <: GList] = IsGNel[A] { type Head = H ; type Tail = T }

  def apply[A](implicit ev: IsGNel[A]): Aux[A, ev.Head, ev.Tail] = ev

  def instance[A, H, T <: GList](
    h: GList.Of[A, H #: T] => H
  ): Aux[A, H, T] = new IsGNel[A] {
    type Head = H
    type Tail = T
    def head(a: GList.Of[A, Head #: Tail]): Head = h(a)
    def tail(a: GList.Of[A, Head #: Tail]): GList.Of[A, Tail] = a.asInstanceOf[GList.Of[A, Tail]]
  }

  object ops {
    implicit final class IsGNelOps[A, H, T <: GList](
      val repr: GList.Of[A, H #: T]
    ) extends AnyVal {
      def head(implicit ev: Aux[A, H, T]): H = ev.head(repr)
      def tail(implicit ev: Aux[A, H, T]): T = ev.tail(repr)
    }
  }
}
