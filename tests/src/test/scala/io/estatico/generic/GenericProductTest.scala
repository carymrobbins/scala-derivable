package io.estatico.generic

import io.estatico.test._
import org.scalatest.{FlatSpec, Matchers}

class GenericProductTest extends FlatSpec with Matchers {

  import GenericProductTest._

  "CsvEncoder" should "be derivable" in {
    //CsvEncoder.encode(Example1("blah")) shouldEqual "blah"
    CsvEncoder.encode(Example2("foo", 3)) shouldEqual "foo,3"
    //CsvEncoder.encode(Example3("hey", 4, 5.2f)) shouldEqual "hey,4,5.2"
    //CsvEncoder.encode(Example4("ya", 2, 8.2f, 7.6)) shouldEqual "ya,2,8.2,7.6"
  }
}

object GenericProductTest {

  //TODO
  //@GenericProduct case class Example1(a: String)
  //implicit val csvEncEx1: CsvEncoder[Example1] = CsvEncoder.generic

  case class Example2(a: String, b: Int)

  implicit val isGNel2Ex2: IsGNel.Aux[Example2, String, Int #: GNil]
    = IsGNel.instance(_.a)

  implicit val isGNel1Ex2: IsGNel.Aux[Example2, Int, GNil]
    = IsGNel.instance(_.b)

  implicit val gProdEx2: GProduct.Aux[Example2, String #: Int #: GNil] = GProduct.instance

  implicit val csvEncEx2: CsvEncoder[Example2] = CsvEncoder.derive[Example2]

  case class Example3(a: String, b: Int, c: Float)

  //implicit val gProdEx3: GProduct.Aux[Example3, String #: Int #: Float #: GNil] = GProduct.instance

  //implicit val csvEncEx3: CsvEncoder[Example3] = CsvEncoder.generic

  case class Example4(a: String, b: Int, c: Float, d: Double)

  //implicit val gProdEx4: GProduct.Aux[Example3, String #: Int #: Float #: Double #: GNil] = GProduct.instance

  //implicit val csvEncEx4: CsvEncoder[Example4] = CsvEncoder.generic

  case class NoExample2(a: String, b: Int)

}
