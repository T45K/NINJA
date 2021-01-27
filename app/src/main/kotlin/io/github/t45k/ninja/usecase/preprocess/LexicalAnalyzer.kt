package io.github.t45k.ninja.usecase.preprocess

import io.github.t45k.ninja.entity.NGrams
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

/**
 * This class performs lexical analysis for counting the number of tokens.
 * This class is used when you want to filter code blocks by min_tokens.
 */
class LexicalAnalyzer {
    fun analyze(text: String): List<Int> =
        ToolFactory.createScanner(false, false, false, "14")
            .also { it.source = text.toCharArray() }
            .let { scanner ->
                generateSequence { 0 }
                    .map { scanner.nextToken }
                    .takeWhile { it != TokenNameEOF }
            }.toList()
}
