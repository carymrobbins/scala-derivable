package io.estatico.generic

trait GenericList[A, H, T] {
  def head(a: A): H
  def tail(a: A): T
}

object GenericList {

  type GL[A, H, T] = GenericList[A, H, T]
  val GF = GenericFields

  def instance[A, H, T >: A](h: A => H): GenericList[A, H, T] = new GenericList[A, H, T] {
    override def head(a: A): H = h(a)
    override def tail(a: A): T = a
  }

  def head[A, H](a: A)(implicit ev: GL[A, H, _]): H = ev.head(a)

  def tail[A, T](a: A)(implicit ev: GL[A, _, T]): T = ev.tail(a)

  private val _gf1: GL[GF._1[Any], Any, GF._0] = instance(_._genArg_1)
  implicit def gf1[A]: GL[GF._1[A], A, GF._0] = _gf1.asInstanceOf[GL[GF._1[A], A, GF._0]]

  private val _gf2: GL[GF._2[Any, Any], Any, GF._1[Any]] = instance(_._genArg_2)
  implicit def gf2[B, A]: GL[GF._2[B, A], B, GF._1[A]] = _gf2.asInstanceOf[GL[GF._2[B, A], B, GF._1[A]]]
}
