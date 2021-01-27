package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.NGramInfo
import io.github.t45k.ninja.entity.NGrams
import io.reactivex.rxjava3.core.Flowable

interface Location {
    fun locate(nGrams: NGrams, index: Int): Flowable<Map.Entry<NGramInfo, Counter>>
    }

/*
TODO: This Counter class is technical debt.
 */
class Counter(var value: Int = 0) {
    fun add(addedValue: Int) {
        value += addedValue
    }
}
