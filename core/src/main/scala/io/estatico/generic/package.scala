package io.estatico

package object generic {
  // Re-exporting of shapeless' Witness for convenience.
  type Witness = shapeless.Witness
  val Witness = shapeless.Witness

  type GField[K, V] = GField.Type[K, V]
}
