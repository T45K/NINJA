package io.github.t45k.ninja.entity

import kotlin.math.min

class InvertedIndex private constructor() {
    companion object {
        /**
         * Alternative constructor
         */
        fun create(
            partitionSize: Int,
            codeBlocks: List<CodeBlock>,
            startIndex: Int
        ): InvertedIndex =
            InvertedIndex().apply {
                val endIndex = min(startIndex + partitionSize, codeBlocks.size)
                for (index in startIndex until endIndex) {
                    val codeBlock = codeBlocks[index]
                    codeBlock.elements.forEach { (element: Element, count: Count) ->
                        hashTable.getOrPut(element) { mutableListOf() }
                            .add(ComparisonUnit(index, codeBlock.numElements, count))
                    }
                }
            }
    }

    private val hashTable = mutableMapOf<Element, MutableList<ComparisonUnit>>()
    operator fun get(key: Element): List<ComparisonUnit> = hashTable[key] ?: emptyList()
}

data class ComparisonUnit(val index: Int, val size: Int, val count: Count)
