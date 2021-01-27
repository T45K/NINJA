package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.Id
import io.github.t45k.ninja.entity.NGrams
import io.reactivex.rxjava3.core.Flowable

class CloneDetection(
    private val locatingPhase: Location,
    private val filteringPhase: Filtration,
    private val nGramsList: List<NGrams>
) {
    fun exec(id: Id): Flowable<Pair<Int, Int>> {
        val nGrams = nGramsList[id]
        val size = nGrams.values.sum()
        return locatingPhase.locate(nGramsList[id], id)
            .filter { filteringPhase.filter(size, it) }
            .map { it.key.id to id }
    }
}
