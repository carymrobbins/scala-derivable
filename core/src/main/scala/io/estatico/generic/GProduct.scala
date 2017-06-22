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

trait IsGCons[A] {
  type Head
  type Tail <: GList
  def head(a: GList.Of[A, Head #: Tail]): Head
  def tail(a: GList.Of[A, Head #: Tail]): GList.Of[A, Tail]
}

object IsGCons {

  type Aux[A, H, T <: GList] = IsGCons[A] { type Head = H ; type Tail = T }

  def apply[A](implicit ev: IsGCons[A]): Aux[A, ev.Head, ev.Tail] = ev

  def instance[A, H, T <: GList](
    h: GList.Of[A, H #: T] => H
  ): Aux[A, H, T] = new IsGCons[A] {
    type Head = H
    type Tail = T
    def head(a: GList.Of[A, Head #: Tail]): Head = h(a)
    def tail(a: GList.Of[A, Head #: Tail]): GList.Of[A, Tail] = a.asInstanceOf[GList.Of[A, Tail]]
  }

  trait Labelled[A] extends IsGCons[A] {
    def headLabel: String
  }

  object Labelled {

    type Aux[A, H, T <: GList] = Labelled[A] { type Head = H ; type Tail = T }

    def apply[A](implicit ev: Labelled[A]): Labelled[A] = ev

    def instance[A, H, T <: GList](
      h: GList.Of[A, H #: T] => H,
      hLabel: String
    ): Aux[A, H, T] = new Labelled[A] {
      type Head = H
      type Tail = T
      def headLabel: String = hLabel
      def head(a: GList.Of[A, H #: T]): H = h(a)
      def tail(a: GList.Of[A, H #: T]): GList.Of[A, T] = a.asInstanceOf[GList.Of[A, T]]
    }
  }

  object ops {

    /**
     * Type inference is weird when extending methods for A, so we have to do some type param trickery
     * with implicit ev to obtain the appropriate type params.
     */
    implicit final class IsGConsOps[A](val repr: A) extends AnyVal {

      def headLabel[AA, H, T <: GList](
        implicit labelled: Labelled.Aux[AA, H, T], ev: A =:= GList.Of[AA, H #: T]
      ): String = labelled.headLabel

      def head[AA, H, T <: GList](
        implicit isGCons: Aux[AA, H, T], ev: A =:= GList.Of[AA, H #: T]
      ): H = isGCons.head(ev(repr))

      def tail[AA, H, T <: GList](
        implicit isGCons: Aux[AA, H, T], ev: A =:= GList.Of[AA, H #: T]
      ): GList.Of[AA, T] = isGCons.tail(ev(repr))
    }
  }
}
