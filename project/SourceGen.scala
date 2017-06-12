import sbt._
import scala.collection.mutable

object SourceGen {

  /** Generates the source files, then returns them. */
  def gen(dir: File): Seq[File] = templates.map { t =>
    val file = t.filename(dir)
    IO.write(file, t.content)
    file
  }

  private val templates = new mutable.ArrayBuffer[Template]

  sealed trait Template {
    // Update templates to include all instances of Template
    templates += this

    def filename(root: File): File
    def content: String
  }

  val genericFieldsTemplate = new Template {

    def filename(root: File): File = root / "io" / "estatico" / "generic" / "GenericFields.scala"

    val content = {

      val gFields = (1 to 22).map { i =>
        val tParamsLast = ('A' + i - 1).toChar
        val tParamsSeq = (tParamsLast.toShort to 'A' by -1).map(_.toChar)
        val tParams = tParamsSeq.mkString(", ")
        val baseClass = s"_${i - 1}" + (
          if (i == 1) ""
          else tParamsSeq.tail.mkString("[", ", ", "]")
        )
        s"trait _$i[$tParams] extends $baseClass { def _genericArgument_$i: $tParamsLast }"
      }.mkString("\n  ")

      val gCtors = (1 to 22).map { i =>
        val tParamsLast = ('A' + i - 1).toChar
        val tParams = (tParamsLast.toShort to 'A' by -1).map(_.toChar).mkString(", ")
        val baseClass = s"Aux[GenericFields._$i[$tParams]] with GenericFields._$i[$tParams]"
        s"trait _$i[$tParams] extends $baseClass { def _genericArgument_$i: $tParamsLast }"
      }.mkString("\n  ")

      s"""
     |package io.estatico.generic
     |
     |object GenericFields {
     |  trait _0
     |  $gFields
     |}
     |
     |trait GenericCtor {
     |  type _GenericFields
     |}
     |
     |object GenericCtor {
     |  trait Aux[F] extends GenericCtor { override final type _GenericFields = F }
     |  trait _0 extends Aux[GenericFields._0] with GenericFields._0
     |  $gCtors
     |}
     |
     """.trim.stripMargin
    }
  }

  val genericListTemplate = new Template {

    def filename(root: File): File = root / "io" / "estatico" / "generic" / "GenericList.scala"

    val content = {

      val gFieldInstances = (1 to 22).map { i =>
        val tParamsLast = ('A' + i - 1).toChar
        val tParamsSeq = (tParamsLast.toShort to 'A' by -1).map(_.toChar)
        val tParams = tParamsSeq.mkString("[", ", ", "]")
        val tParamsTail = tParamsSeq.tail match {
          case ts if ts.isEmpty => ""
          case ts => ts.mkString("[", ", ", "]")
        }
        val allAnyTParamsSeq = Iterator.fill(i)("Any").toSeq
        val allAnyTParams = allAnyTParamsSeq.mkString("[", ", ", "]")
        val tailAnyTParams = allAnyTParamsSeq.tail match {
          case ts if ts.isEmpty => ""
          case ts => ts.mkString("[", ", ", "]")
        }
        val retType = s"GenericList[GenericFields._$i$tParams, $tParamsLast, GenericFields._${i - 1}$tParamsTail]"
        Seq(
          // Cached instance
          s"private val _cachedField$i: GenericList[" +
            s"GenericFields._$i$allAnyTParams, Any, GenericFields._${i - 1}$tailAnyTParams" +
          s"] = instance(_._genericArgument_$i)",

          // Typed instance
          s"implicit def field$i$tParams: $retType = _cachedField$i.asInstanceOf[$retType]",

          // Blank line between instances
          ""
        )
      }.flatten.mkString("\n  ")

      s"""
     |package io.estatico.generic
     |
     |trait GenericList[A, H, T] {
     |  def head(a: A): H
     |  def tail(a: A): T
     |}
     |
     |object GenericList {
     |
     |  def instance[A, H, T >: A](h: A => H): GenericList[A, H, T] = new GenericList[A, H, T] {
     |    override def head(a: A): H = h(a)
     |    override def tail(a: A): T = a
     |  }
     |
     |  def head[A, H](a: A)(implicit ev: GenericList[A, H, _]): H = ev.head(a)
     |
     |  def tail[A, T](a: A)(implicit ev: GenericList[A, _, T]): T = ev.tail(a)
     |
     |  implicit def ctor[C <: GenericCtor, H, T](
     |    implicit ev: GenericList[C#_GenericFields, H, T]
     |  ): GenericList[C, H, T] = ev.asInstanceOf[GenericList[C, H, T]]
     |
     |  $gFieldInstances
     |}
     """.trim.stripMargin
    }
  }
}
