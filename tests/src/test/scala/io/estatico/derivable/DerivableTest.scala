//package io.estatico.derivable
//
//import io.estatico.test._
//import org.scalatest.{FlatSpec, Matchers}
//
//class DerivableTest extends FlatSpec with Matchers {
//
//  "CsvEncoder" should "work with manual macro" in {
//    import CsvEncoderInstances._
//    case class Foo(i: Int, s: String)
//    val csvEncoder = CsvEncoder.deriveProductManualMacro[Foo]
//    csvEncoder.encode(Foo(1, "foo")) shouldEqual "1,foo"
//  }
//}
