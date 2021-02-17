package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.CodeBlock
import io.github.t45k.ninja.entity.InvertedIndex
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import kotlin.math.min

class NGramBasedLocation(private val invertedIndex: InvertedIndex) {
    fun locate(codeBlock: CodeBlock, index: Int): Flowable<Map.Entry<Int, Counter>> =
        mutableMapOf<Int, Counter>().apply {
            codeBlock.elements.forEach { (element, count) ->
                invertedIndex[element]
                    .asSequence()
                    .filter { index > it.index }
                    .forEach {
                        getOrPut(it.index) { Counter() }.add(min(count, it.count))
                    }
            }
        }
            .asSequence()
            .toFlowable()
}

class Counter(var value: Int = 0) {
    fun add(addedValue: Int) {
        value += addedValue
    }
}
