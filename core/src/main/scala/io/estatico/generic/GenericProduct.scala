package io.estatico.generic

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

class GenericProduct extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro GenericProductMacros.impl
}

@macrocompat.bundle
private[generic] final class GenericProductMacros(val c: blackbox.Context) {

  import c.universe._

  private def fail(message: String) = c.abort(c.enclosingPosition, message)

  def impl(annottees: Tree*): Tree = annottees match {
    case List(clsDef: ClassDef) => q"${updateClassDef(clsDef)}"
    case List(clsDef: ClassDef, modDef: ModuleDef) => updateClassDef(clsDef, modDef)
    case List(modDef: ModuleDef, clsDef: ClassDef) => updateClassDef(clsDef, modDef)
    case _ => fail("Invalid usage of annotation, only applies to classes")
  }

  private def updateClassDef(clsDef: ClassDef): ClassDef = {
    val ClassDef(mods, name, params, Template(parents, self, body)) = clsDef
    val fields = body.collect {
      case v: ValDef if v.mods.hasFlag(Flag.PARAMACCESSOR) && !v.mods.hasFlag(Flag.PRIVATE) => v
    }
    val len = fields.length
    if (len == 0) {
      fail("No param accessor fields found, if this is intentional, extend from GenericCtor._0 instead.")
    }
    val gFields = (len to 1 by -1).zip(fields).map { case (i, f) =>
      val name = TermName(s"_genericArgument_$i")
      q"override def $name = this.${f.name}"
    }
    val gTypes = fields.map(f => f.tpt)
    val ctor = tq"_root_.io.estatico.generic.GenericCtor.${TypeName(s"_$len")}[..$gTypes]"
    val newParents = parents :+ ctor
    val newBody = body ++ gFields
    val result = ClassDef(mods, name, params, Template(newParents, self, newBody))
    result
  }

  private def updateClassDef(clsDef: ClassDef, modDef: ModuleDef): Tree = {
    q"""
      ${updateClassDef(clsDef)}
      $modDef
    """
  }
}
