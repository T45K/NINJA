package io.github.t45k.ninja.entity

data class CodeBlock(
    val fileId: Long,
    val startLine: Int,
    val endLine: Int,
    val numElements: Int, // This value is sum of elements except for '}'
    // val language: Int,
    // val granularity: Int,
    val elements: List<ElementFrequency>,
)

typealias Element = Int
typealias Count = Int
typealias ElementFrequency = Pair<Element, Count>
