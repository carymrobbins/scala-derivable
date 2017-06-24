package io.estatico.generic

import java.util.concurrent.TimeUnit

import io.circe._
import io.estatico.generic.DerivableCirceBenchmarkFixture._
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
    import io.estatico.test.ShapelessCirce._
    implicit val barEncoder: Encoder[Bar] = deriveEncoder[Bar]
    implicit val fooEncoder: Encoder[Foo] = deriveEncoder[Foo]
    encode(fixture)
  }

  @Benchmark
  def derivableCirce(): Unit = {
    import io.estatico.test.DerivableCirce._
    implicit val barEncoder: Encoder[Bar] = deriveEncoder[Bar]
    implicit val fooEncoder: Encoder[Foo] = deriveEncoder[Foo]
    encode(fixture)
  }
}
