package io.estatico.generic

import io.circe._
import io.estatico.test.DerivableCirce._
import org.scalatest.{FlatSpec, Matchers}

class DeriveLabelledGProductTest extends FlatSpec with Matchers {

  import DeriveLabelledGProductTest._

  "Circe Encoder" should "be derivable" in {
    encode(Example1("blah")) shouldEqual """{"a":"blah"}"""
    encode(Example2("foo", 3)) shouldEqual """{"a":"foo","b":3}"""
    encode(Example3("hey", 4, 5.2f)) shouldEqual """{"a":"hey","b":4,"c":5.2}"""
    encode(Example4("ya", 2, 8.2f, 7.6)) shouldEqual """{"a":"ya","b":2,"c":8.2,"d":7.6}"""
  }

  private def encode[A](a: A)(implicit ev: Encoder[A]) = ev(a).noSpaces
}

object DeriveLabelledGProductTest {

  @DeriveLabelledGProduct case class Example1(a: String)
  implicit val csvEncEx1: Encoder[Example1] = deriveEncoder[Example1]

  @DeriveLabelledGProduct case class Example2(a: String, b: Int)
  implicit val csvEncEx2: Encoder[Example2] = deriveEncoder[Example2]

  @DeriveLabelledGProduct case class Example3(a: String, b: Int, c: Float)
  implicit val csvEncEx3: Encoder[Example3] = deriveEncoder[Example3]

  @DeriveLabelledGProduct case class Example4(a: String, b: Int, c: Float, d: Double)
  implicit val csvEncEx4: Encoder[Example4] = deriveEncoder[Example4]
}
