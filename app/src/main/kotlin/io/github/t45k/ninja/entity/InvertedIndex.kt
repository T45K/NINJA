package io.github.t45k.ninja.entity

import kotlin.math.min

class InvertedIndex private constructor() {
    companion object {
        /**
         * Alternative constructor
         */
        fun create(
            partitionSize: Int,
            nGramsList: List<NGrams>,
            startIndex: Int
        ): InvertedIndex =
            InvertedIndex().apply {
                val endIndex = min(startIndex + partitionSize, nGramsList.size)
                for (index in startIndex until endIndex) {
                    val nGrams = nGramsList[index]
                    val size = nGrams.values.sum()
                    nGrams.forEach { (nGram: NGram, count: Count) ->
                        hashTable.getOrPut(nGram) { mutableListOf() }.add(nGram to size to count)
                    }
                }
            }
    }

    private val hashTable = mutableMapOf<NGram, MutableList<Pair<NGramInfo, Count>>>()
    operator fun get(key: NGram): List<Pair<NGramInfo, Count>> = hashTable[key] ?: emptyList()
}
