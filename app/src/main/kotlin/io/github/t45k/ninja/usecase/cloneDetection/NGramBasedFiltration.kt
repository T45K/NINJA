package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.NGramInfo
import kotlin.math.max

/**
 * Using Jaccard Similarity
 */
class NGramBasedFiltration(private val threshold: Int) : Filtration {
    override fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Counter>): Boolean {
        val max = max(nGramSize, cloneCandidate.key.size)
        return cloneCandidate.value.value * 100 / max >= threshold
    }
}
