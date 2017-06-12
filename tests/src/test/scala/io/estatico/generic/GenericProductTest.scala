package io.estatico.generic

import io.estatico.test._
import org.scalatest.{FlatSpec, Matchers}

class GenericProductTest extends FlatSpec with Matchers {

  import GenericProductTest._

  "CsvEncoder" should "be derivable" in {
    CsvEncoder[ExampleCase].encode(ExampleCase("foo", 3)) shouldEqual "foo,3"
  }
}

object GenericProductTest {

  @GenericProduct
  case class ExampleCase(a: String, b: Int)

  implicit val csvEncEx: CsvEncoder[ExampleCase] = CsvEncoder.derive[ExampleCase]
}
