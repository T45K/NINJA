package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.CodeBlock
import io.reactivex.rxjava3.core.Flowable

class CloneDetection(
    private val locatingPhase: NGramBasedLocation,
    private val filteringPhase: NGramBasedFiltration,
    private val codeBlocks: List<CodeBlock>
) {
    fun exec(index: Int): Flowable<Pair<Int, Int>> =
        locatingPhase.locate(codeBlocks[index], index)
            .filter { filteringPhase.filter(index, it) }
            .map { it.key to index }
}
