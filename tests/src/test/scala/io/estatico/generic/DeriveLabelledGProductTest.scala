package io.estatico.generic

import io.circe._
import io.estatico.test.DerivableCirce
import org.scalatest.{FlatSpec, Matchers}

class DeriveLabelledGProductTest extends FlatSpec with Matchers {

  import DeriveLabelledGProductTest._

  "Circe Encoder" should "be derivable" in {
    encode(Example1("blah")).noSpaces shouldEqual """{"a":"blah"}"""
  }

  private def encode[A](a: A)(implicit ev: Encoder[A]): Json = ev(a)
}

object DeriveLabelledGProductTest {

  @DeriveLabelledGProduct case class Example1(a: String)
  implicit val csvEncEx1: Encoder[Example1] = DerivableCirce.encoder[Example1]

//  @DeriveGProduct case class Example2(a: String, b: Int)
//  implicit val csvEncEx2: Encoder[Example2] = DerivableCirce.encoder[Example2]
//
//  @DeriveGProduct case class Example3(a: String, b: Int, c: Float)
//  implicit val csvEncEx3: Encoder[Example3] = DerivableCirce.encoder[Example3]
//
//  @DeriveGProduct case class Example4(a: String, b: Int, c: Float, d: Double)
//  implicit val csvEncEx4: Encoder[Example4] = DerivableCirce.encoder[Example4]
//
//  @DeriveGProduct case class NoExample1(a: String)
//  @DeriveGProduct case class NoExample2(a: String, b: Int)
//  @DeriveGProduct case class NoExample3(a: String, b: Int, c: Float)
//  @DeriveGProduct case class NoExample4(a: String, b: Int, c: Float, d: Double)
}
