//package io.estatico.derivable
//
//import scala.annotation.StaticAnnotation
//import scala.reflect.macros.whitebox
//
//class deriver extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro DerivableMacros.derive
//}
//
//@macrocompat.bundle
//class DerivableMacros(val c: whitebox.Context) {
//
//  import c.universe._
//
//  def derive(annottees: Expr[Any]*) = {
//    //println("derive stuff!")
//    //println("show: " + show(annottees))
//    //println("showRaw: " + showRaw(annottees))
//
//    annottees.foreach(
//      xs => xs.tree.foreach(x => println(s"TREE: ${show(x)}"))
//    )
//
//    annottees.foreach(x => println(s"annottee: ${show(x)}"))
//
//    c.abort(c.enclosingPosition, "TODO: Implement derive")
//  }
//
//  private def rewriteCaseFields(annottees: Seq[Expr[Any]]) = {
//    annottees.map { e =>
//      e.tree.find(t => t.is)
//    }
//  }
//}
//
////class caseFields(val c: whitebox.Context) {
////
////  import c.universe._
////
////  def apply[A](implicit tag: WeakTypeTag[A]): CaseFieldsImpl[A] = {
////    val A = weakTypeOf[A]
////    if (isCaseClass(A)) {
////      new CaseFieldsImpl(c)(tag)(getCaseFields(A))
////    } else {
////      c.abort(c.enclosingPosition, s"$A is not a case class")
////    }
////  }
////
////  private def isCaseClass(t: Type): Boolean = {
////    val s = t.typeSymbol
////    s.isClass && s.asClass.isCaseClass
////  }
////
////  private def getCaseFields(t: Type): Iterator[(TermName, Type)] = {
////    t.decls.collect { case m: MethodSymbol if m.isCaseAccessor =>
////      (m.name, m.returnType)
////    }.iterator
////  }
////}
