package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.InvertedIndex
import io.github.t45k.ninja.entity.NGramInfo
import io.github.t45k.ninja.entity.NGrams
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import kotlin.math.min

class NGramBasedLocation(private val invertedIndex: InvertedIndex) : Location {
    override fun locate(nGrams: NGrams, index: Int): Flowable<Map.Entry<NGramInfo, Counter>> =
        mutableMapOf<NGramInfo, Counter>().apply {
            nGrams.forEach { (nGram, count) ->
                invertedIndex[nGram]
                    .asSequence()
                    .filter { index > it.first.id }
                    .forEach {
                        getOrPut(it.first) { Counter() }.add(min(count, it.second))
                    }
            }
        }
            .asSequence()
            .toFlowable()
}
