package io.estatico.generic

import io.estatico.test._
import org.scalatest.{FlatSpec, Matchers}

class GenericProductTest extends FlatSpec with Matchers {

  import GenericProductTest._

  "CsvEncoder" should "be derivable" in {
    //TODO
    //CsvEncoder.encode(Example1("blah")) shouldEqual "blah"
    CsvEncoder.encode(Example2("foo", 3)) shouldEqual "foo,3"
    CsvEncoder.encode(Example3("hey", 4, 5.2f)) shouldEqual "hey,4,5.2"
    CsvEncoder.encode(Example4("ya", 2, 8.2f, 7.6)) shouldEqual "ya,2,8.2,7.6"
  }

  //TODO
  //it should "not auto-derive" in {
  //  assertCompiles("CsvEncoder[Example2]")
  //  assertDoesNotCompile("CsvEncoder[NoExample2]")
  //}
}

object GenericProductTest {

  //TODO
  //@GenericProduct case class Example1(a: String)
  //implicit val csvEncEx1: CsvEncoder[Example1] = CsvEncoder.generic

  @GenericProduct case class Example2(a: String, b: Int)
  implicit val csvEncEx2: CsvEncoder[Example2] = CsvEncoder.generic

  @GenericProduct case class Example3(a: String, b: Int, c: Float)
  implicit val csvEncEx3: CsvEncoder[Example3] = CsvEncoder.generic

  @GenericProduct case class Example4(a: String, b: Int, c: Float, d: Double)
  implicit val csvEncEx4: CsvEncoder[Example4] = CsvEncoder.generic

  @GenericProduct case class NoExample2(a: String, b: Int)
}
