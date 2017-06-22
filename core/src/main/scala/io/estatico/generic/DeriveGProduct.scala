package io.estatico.generic

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

class DeriveGProduct extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro DeriveGProductMacros.impl
}

@macrocompat.bundle
private[generic] final class DeriveGProductMacros(val c: blackbox.Context) {

  import c.universe._

  private def fail(message: String) = c.abort(c.enclosingPosition, message)

  def impl(annottees: Tree*): Tree = annottees match {
    case List(clsDef: ClassDef) =>
      q"""
        $clsDef
        object ${clsDef.name.toTermName} {
          ..${derivedInstances(clsDef)}
        }
      """

    case List(
      clsDef: ClassDef,
      q"object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf => ..$objDefs }"
    ) =>
      q"""
        $clsDef
        object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf =>
          ..$objDefs
          ..${derivedInstances(clsDef)}
        }
      """

    case _ => c.abort(c.enclosingPosition, s"Only case classes are supported.")
  }

  private def derivedInstances(clsDef: ClassDef) = {
    val clsName = clsDef.name
    val clsBody = clsDef.impl.body
    val fields = clsBody.collect {
      case v: ValDef if v.mods.hasFlag(Flag.PARAMACCESSOR) && !v.mods.hasFlag(Flag.PRIVATE) => v
    }
    val len = fields.length
    val gCons = (len to 1 by -1).map { i =>
      val name = TermName(s"isGCons$i")
      val f +: fs = fields.takeRight(i)
      val gHead = f.tpt
      val gTail = mkGList(fs.map(_.tpt))
      val retType = tq"_root_.io.estatico.generic.IsGCons.Aux[$clsName, $gHead, $gTail]"
      val retVal  = q"_root_.io.estatico.generic.IsGCons.instance(_.${f.name})"
      q"implicit val $name: $retType = $retVal"
    }
    val gProd = {
      val gList = mkGList(fields.map(_.tpt))
      val retType = tq"_root_.io.estatico.generic.GProduct.Aux[$clsName, $gList]"
      val retVal  = q"_root_.io.estatico.generic.GProduct.instance"
      q"implicit val gProduct: $retType = $retVal"
    }
    gCons :+ gProd
  }

  private def mkGList(types: List[Tree]): Tree = {
    types.foldRight(
      tq"_root_.io.estatico.generic.GNil": Tree
    )((t, acc) =>
      tq"_root_.io.estatico.generic.#:[$t, $acc]"
    )
  }
}
