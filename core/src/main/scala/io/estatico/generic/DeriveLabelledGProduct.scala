package io.estatico.generic

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

class DeriveLabelledGProduct extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro DeriveLabelledGProductMacros.impl
}

@macrocompat.bundle
private[generic] final class DeriveLabelledGProductMacros(val c: blackbox.Context) {

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
      val name = TermName(s"isGConsLabelled$i")
      val f +: fs = fields.takeRight(i)
      val gHead = f.tpt
      val gHeadLabel = f.name.decodedName.toString.trim
      val gTail = mkGList(fs)
      val retType = tq"_root_.io.estatico.generic.IsGCons.Labelled.Aux[$clsName, $gHead, $gTail]"
      val retVal  = q"""
        _root_.io.estatico.generic.IsGCons.Labelled.instance(
          _.${f.name}.asInstanceOf[$gHead],
          $gHeadLabel
        )
      """
      q"implicit val $name: $retType = $retVal"
    }
    val gProd = {
      val gList = mkGList(fields)
      val retType = tq"_root_.io.estatico.generic.LabelledGProduct.Aux[$clsName, $gList]"
      val retVal  = q"_root_.io.estatico.generic.LabelledGProduct.instance"
      q"implicit val gProductLabelled: $retType = $retVal"
    }
    gCons :+ gProd
  }

  private def mkGList(fields: List[ValDef]): Tree = {
    fields.foldRight(
      tq"_root_.io.estatico.generic.GNil": Tree
    )((f, acc) =>
      tq"_root_.io.estatico.generic.#:[${f.tpt}, $acc]"
    )
  }
}
