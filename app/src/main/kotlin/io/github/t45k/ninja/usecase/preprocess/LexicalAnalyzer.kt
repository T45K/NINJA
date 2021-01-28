package io.github.t45k.ninja.usecase.preprocess

import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

/**
 * This class performs lexical analysis for counting the number of tokens.
 * This class is used when you want to filter code blocks by min_tokens.
 */
class LexicalAnalyzer {
    fun analyze(text: String): List<Int> {
        val tokenSequence = ToolFactory.createScanner(false, false, false, "14")
            .also { it.source = text.toCharArray() }
            .let { scanner ->
                generateSequence { 0 }
                    .map { scanner.nextToken }
                    .takeWhile { it != TokenNameEOF }
            }.toList()
        return mutableListOf<Int>()
            .apply {
                var start = 0
                for (end in tokenSequence.indices) {
                    if (tokenSequence[end] == ITerminalSymbols.TokenNameLBRACE ||
                        tokenSequence[end] == ITerminalSymbols.TokenNameRBRACE ||
                        tokenSequence[end] == ITerminalSymbols.TokenNameSEMICOLON
                    ) {
                        if (start != end) {
                            add(tokenSequence.subList(start, end).hashCode())
                        }
                        start = end + 1
                    }
                }
            }
    }
}
