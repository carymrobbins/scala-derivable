package io.estatico.generic

trait GenericCtor {
  type _GenFields
}

object GenericCtor {

  implicit def toGenFields[C <: GenericCtor](c: C): C#_GenFields = c.asInstanceOf[C#_GenFields]

  trait Aux[F] extends GenericCtor { override final type _GenFields = F }
  trait _0 extends Aux[GenericFields._0] with GenericFields._0
  trait _1[A] extends Aux[GenericFields._1[A]] with GenericFields._1[A]
  trait _2[B, A] extends Aux[GenericFields._2[B, A]] with GenericFields._2[B, A]
  trait _3[C, B, A] extends Aux[GenericFields._3[C, B, A]] with GenericFields._3[C, B, A]
  trait _4[D, C, B, A] extends Aux[GenericFields._4[D, C, B, A]] with GenericFields._4[D, C, B, A]
  trait _5[E, D, C, B, A] extends Aux[GenericFields._5[E, D, C, B, A]] with GenericFields._5[E, D, C, B, A]
}

object GenericFields {
  trait _0
  trait _1[A]                 extends _0                { def _genArg_1: A }
  trait _2[B, A]              extends _1[A]             { def _genArg_2: B }
  trait _3[C, B, A]           extends _2[B, A]          { def _getArg_3: C }
  trait _4[D, C, B, A]        extends _3[C, B, A]       { def _getArg_4: D }
  trait _5[E, D, C, B, A]     extends _4[D, C, B, A]    { def _getArg_5: E }
}
