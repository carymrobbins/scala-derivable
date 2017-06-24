# Derivable

A compile time approach to generic programming in Scala.

## Motivation

The [shapeless](https://github.com/milessabin/shapeless) library
is the obvious inspiration for this project. The difference is that
shapeless creates intermediate data structures (HLists) when operating
on Product types. However, this library tests the hypothesis that you
don't actually need to do that; instead, you can attach type information
using tagged types and leverage this at compile time to perform generic
operations.

For example, here is how we could covert a data structure to its generic
representation using shapeless.

```scala
import shapeless._

case class Foo(a: Int, b: String)

val foo = Foo(1, "hey")
val g = Generic[Foo].to(foo)
g: Int :: String :: HNil
foo == g // false
```

What's really happening is we are constructing two instances of the `::` class.
As such, we understand that we must allocate a new `::` class for each field
in our class we which to convert to a generic representation. For explicitness,
I've demonstrated this with the canonical way to construct HLists (using `::` as
an operator) as well as with explicit `new` calls.

```scala
(1 :: "hey" :: HNil).equals(g) // true
new ::(1, new ::("hey", HNil)).equals(g) // true
```

Alternatively, the derivable project allows us to do this -

```scala
import io.estatico.generic._

@DeriveGProduct
case class Foo(a: Int, b: String)

val foo = Foo(1, "hey")
val g = GProduct.to(foo)
g: Foo with (Int #: String #: GNil)
g: GList.Of[Foo, Int #: String #: GNil] // Alias for the above type.
foo.equals(g) // true
```

All the `GProduct.to` method does is tag `foo` with the type `Int #: String #: GNil`.
It currently has an `@inline` annotation, so when compiled with the appropriate
optimization level, the call should be eliminated at runtime (even though it's pretty
inexpensive call to begin with which may even be eliminated due to the JIT).

Instead of pattern matching on HList values as shapeless does, derivable uses
type classes to handle the tagged GList type.

```scala
IsGCons.Aux[Foo, Int, String #: GNil].head(g)
// Int = 1

val t = IsGCons.Aux[Foo, Int, String #: GNil].tail(g)
// GList.Of[Foo, String #: GNil] = Foo(1,hey)

IsGCons.Aux[Foo, String, GNil].head(t)
// String = hey

IsGCons.Aux[Foo, String, GNil].tail(t)
// GList.Of[Foo, GNil] = Foo(1,hey)
```

See [CsvEncoder](/tests/src/main/scala/io/estatico/test/CsvEncoder.scala)
and [DerivableCirce](/tests/src/main/scala/io/estatico/test/DerivableCirce.scala)
for examples of using `GProduct` and `LabelledGProduct`.

## Benchmarks

The benchmarks take a real world use case - encoding JSON. I've chosen to
use the [circe](https://github.com/circe/circe) library as our example.
Instead of using circe's built-in derivation (which uses optimizations via
macros) I've implemented near identical solutions in
[DerivableCirce.scala](/tests/src/main/scala/io/estatico/test/DerivableCirce.scala)
using derivable and shapeless.

You can run the benchmarks using this SBT command -

```
> benchmark/jmh:run -i 50 -wi 50 -f 3 -t max io.estatico.generic.DerivableCirceBenchmark
```

Here is the sample output on my machine -

```
Benchmark                                Mode  Cnt      Score     Error  Units
DerivableCirceBenchmark.manualCirce     thrpt  150  18195.261 ± 252.395  ops/s
DerivableCirceBenchmark.shapelessCirce  thrpt  150  16623.818 ± 332.174  ops/s
DerivableCirceBenchmark.derivableCirce  thrpt  150  18223.600 ± 184.000  ops/s
```
