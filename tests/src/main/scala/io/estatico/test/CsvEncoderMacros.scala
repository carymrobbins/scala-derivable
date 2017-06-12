package io.estatico.test

import scala.reflect.macros.blackbox

@macrocompat.bundle
class CsvEncoderMacros(val c: blackbox.Context) {

  import c.universe._

  def deriveProductImpl[A : WeakTypeTag]: Tree = {
    val A = weakTypeOf[A]
    if (!isCaseClass(A)) c.abort(c.enclosingPosition, "can only derive for case classes")
    val impl = caseFields(A).map { case (n, t) =>
      q"_root_.io.estatico.test.CsvEncoder[$t].encode(x.$n)"
    }.reduce((lhs, rhs) => q"$lhs + ',' + $rhs")
    q"_root_.io.estatico.test.CsvEncoder.instance(x => $impl): CsvEncoder[$A]"
  }

  private def isCaseClass(t: Type): Boolean = {
    val s = t.typeSymbol
    s.isClass && s.asClass.isCaseClass
  }

  private def caseFields(t: Type): Iterator[(TermName, Type)] = {
    t.decls.collect { case m: MethodSymbol if m.isCaseAccessor =>
      (m.name, m.returnType)
    }.iterator
  }
}
