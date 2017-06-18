package io.estatico.generic

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

class DeriveGeneric extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro DeriveGenericMacros.impl
}

@macrocompat.bundle
private[generic] final class DeriveGenericMacros(val c: blackbox.Context) {

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
    val gNels = (len to 1 by -1).map { i =>
      val name = TermName(s"isGNel$i")
      val f +: fs = fields.takeRight(i)
      val gHead = f.tpt
      val gTail = mkGList(fs.map(_.tpt))
      val retType = tq"_root_.io.estatico.generic.IsGNel.Aux[$clsName, $gHead, $gTail]"
      val retVal  = q"_root_.io.estatico.generic.IsGNel.instance(_.${f.name})"
      q"implicit val $name: $retType = $retVal"
    }
    val gProd = {
      val gList = mkGList(fields.map(_.tpt))
      val retType = tq"_root_.io.estatico.generic.GProduct.Aux[$clsName, $gList]"
      val retVal  = q"_root_.io.estatico.generic.GProduct.instance"
      q"implicit val gProduct: $retType = $retVal"
    }
    gNels :+ gProd
  }

  private def mkGList(types: List[Tree]): Tree = {
    types.foldRight(
      tq"_root_.io.estatico.generic.GNil": Tree
    )((t, acc) =>
      tq"_root_.io.estatico.generic.#:[$t, $acc]"
    )
  }
}
