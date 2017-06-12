package io.estatico.generic

import io.estatico.test._
import org.scalatest.{FlatSpec, Matchers}

class GenericCtorsTest extends FlatSpec with Matchers {

  import GenericCtorsTest._

  "CsvEncoder" should "be derivable" in {
    CsvEncoder[ExampleCase].encode(ExampleCase("foo", 3)) shouldEqual "foo,3"
  }
}

object GenericCtorsTest {

  // TODO: Add a @generic annotation for generating inheritance
  case class ExampleCase(a: String, b: Int)
    extends GenericCtor._2[String, Int] {
    override def _genArg_2 = a
    override def _genArg_1 = b
  }

  import CsvEncoderInstances._

  implicit val csvEncEx: CsvEncoder[ExampleCase] = CsvEncoder.derive[ExampleCase]
}
