# Derivable

A compile time approach to generic programming in Scala.

## Motivation

The [shapeless](https://github.com/milessabin/shapeless) library
is the obvious inspiration for this technique. The difference is that
shapeless creates an intermediate data structure (HList) when operating
on Product types. However, this library tests the hypothesis that you
don't actually need to do that; instead, you can attach type information
using tagged types and leverage this at compile time to perform generic
operations.

## Benchmarks

The benchmarks take a real world use case - encoding JSON. I've chosen to
use the [circe](https://github.com/circe/circe) library as our example.
Instead of using circe's built-in derivation (which uses optimizations via
macros) I've implemented
[near identical implementations](tests/src/main/scala/io/estatico/test/DerivableCirce.scala)
with derivable and shapeless.

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
