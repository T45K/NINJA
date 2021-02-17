package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.CodeBlock
import kotlin.math.max

/**
 * Using Jaccard Similarity
 */
class NGramBasedFiltration(private val threshold: Int, private val codeBlocks: List<CodeBlock>) {
    fun filter(baseIndex: Int, cloneCandidate: Map.Entry<Int, Counter>): Boolean {
        val max = max(codeBlocks[baseIndex].numElements, codeBlocks[cloneCandidate.key].numElements)
        return cloneCandidate.value.value * 100 / max >= threshold
    }
}
