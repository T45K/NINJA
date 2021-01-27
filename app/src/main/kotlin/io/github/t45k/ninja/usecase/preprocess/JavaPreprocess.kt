package io.github.t45k.ninja.usecase.preprocess

import io.github.t45k.ninja.NinjaConfig
import io.github.t45k.ninja.entity.CodeBlock
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import java.io.File

class JavaPreprocess(private val config: NinjaConfig) : Preprocess(config.threads) {
    override fun collectSourceFiles(dir: File): Flowable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toFlowable()

    override fun collectBlocks(srcFile: File): Flowable<CodeBlock> =
        Flowable.just(srcFile)
            .flatMap { JavaParser(LexicalAnalyzer()::analyze, config).extractBlocks(it) }
}
