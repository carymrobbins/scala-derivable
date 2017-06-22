package io.estatico.generic

import java.util.concurrent.TimeUnit

import io.circe._
import io.estatico.generic.DerivableCirceBenchmarkFixture._
import io.estatico.test.DerivableCirce._
import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class DerivableCirceBenchmark {

  @Benchmark
  def manualCirce(): Unit = {
    implicit val encoder = manualFooEncoder
    encode(fixture)
  }

  @Benchmark
  def shapelessCirce(): Unit = {
    implicit val barEncoder: Encoder[Bar] = io.circe.generic.semiauto.deriveEncoder[Bar]
    implicit val fooEncoder: Encoder[Foo] = io.circe.generic.semiauto.deriveEncoder[Foo]
    encode(fixture)
  }

  @Benchmark
  def derivableCirce(): Unit = {
    implicit val barEncoder: Encoder[Bar] = deriveEncoder[Bar]
    implicit val fooEncoder: Encoder[Foo] = deriveEncoder[Foo]
    encode(fixture)
  }
}
