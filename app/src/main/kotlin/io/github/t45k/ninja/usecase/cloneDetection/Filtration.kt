package io.github.t45k.ninja.usecase.cloneDetection

import io.github.t45k.ninja.entity.NGramInfo

interface Filtration {
    fun filter(nGramSize: Int, cloneCandidate: Map.Entry<NGramInfo, Counter>): Boolean
}
