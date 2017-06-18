package io.estatico.generic

import io.estatico.test._
import org.scalatest.{FlatSpec, Matchers}

class DeriveGProductTest extends FlatSpec with Matchers {

  import DeriveGProductTest._

  "CsvEncoder" should "be derivable" in {
    CsvEncoder.encode(Example1("blah")) shouldEqual "blah"
    CsvEncoder.encode(Example2("foo", 3)) shouldEqual "foo,3"
    CsvEncoder.encode(Example3("hey", 4, 5.2f)) shouldEqual "hey,4,5.2"
    CsvEncoder.encode(Example4("ya", 2, 8.2f, 7.6)) shouldEqual "ya,2,8.2,7.6"
  }

  it should "not auto-derive" in {
    assertCompiles("CsvEncoder[Example1]")
    assertCompiles("CsvEncoder[Example2]")
    assertCompiles("CsvEncoder[Example3]")
    assertCompiles("CsvEncoder[Example4]")
    assertDoesNotCompile("CsvEncoder[NoExample1]")
    assertDoesNotCompile("CsvEncoder[NoExample2]")
    assertDoesNotCompile("CsvEncoder[NoExample3]")
    assertDoesNotCompile("CsvEncoder[NoExample4]")
  }
}

object DeriveGProductTest {

  @DeriveGProduct case class Example1(a: String)
  implicit val csvEncEx1: CsvEncoder[Example1] = CsvEncoder.derive[Example1]

  @DeriveGProduct case class Example2(a: String, b: Int)
  implicit val csvEncEx2: CsvEncoder[Example2] = CsvEncoder.derive[Example2]

  @DeriveGProduct case class Example3(a: String, b: Int, c: Float)
  implicit val csvEncEx3: CsvEncoder[Example3] = CsvEncoder.derive[Example3]

  @DeriveGProduct case class Example4(a: String, b: Int, c: Float, d: Double)
  implicit val csvEncEx4: CsvEncoder[Example4] = CsvEncoder.derive[Example4]

  @DeriveGProduct case class NoExample1(a: String)
  @DeriveGProduct case class NoExample2(a: String, b: Int)
  @DeriveGProduct case class NoExample3(a: String, b: Int, c: Float)
  @DeriveGProduct case class NoExample4(a: String, b: Int, c: Float, d: Double)
}
