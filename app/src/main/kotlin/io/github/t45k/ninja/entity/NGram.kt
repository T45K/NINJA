package io.github.t45k.ninja.entity

/**
 * NGram is a hash value of N-gram
 */
typealias NGram = Int

/**
 * NGrams is a dictionary whose a key is NGram and a value is count.
 */
typealias NGrams = Map<NGram,Count>

/**
 * NGramInfo is pair of
 * Id: the index of token sequence of N-gram
 * Size: the size of a distinct set of N-gram
 */
data class NGramInfo(val id: Id, val size: Size)

infix fun Id.to(size: Size): NGramInfo = NGramInfo(this, size)

typealias Id = Int
typealias Size = Int
typealias Count = Int
