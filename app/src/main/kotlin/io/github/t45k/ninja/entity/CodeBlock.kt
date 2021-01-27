package io.github.t45k.ninja.entity

/**
 * Code block is a single function.
 */
data class CodeBlock(
    val fileName: String,
    val startLine: Int,
    val endLine: Int,
    val ngrams: NGrams,
) {
    override fun toString(): String =
        "${fileName},${startLine},${endLine}"
}
