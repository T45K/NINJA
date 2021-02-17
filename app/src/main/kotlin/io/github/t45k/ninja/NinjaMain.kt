package io.github.t45k.ninja

import io.github.t45k.ninja.entity.CodeBlock
import io.github.t45k.ninja.entity.ElementFrequency
import io.github.t45k.ninja.entity.InvertedIndex
import io.github.t45k.ninja.presenter.logger.LoggerWrapperFactory
import io.github.t45k.ninja.usecase.cloneDetection.CloneDetection
import io.github.t45k.ninja.usecase.cloneDetection.NGramBasedFiltration
import io.github.t45k.ninja.usecase.cloneDetection.NGramBasedLocation
import io.github.t45k.ninja.util.parallelIfSpecified
import io.github.t45k.ninja.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class NinjaMain(private val config: NinjaConfig) {
    companion object {
        const val CLONE_PAIR_FILE_NAME = "clone_pairs"
    }

    private val logger =
        LoggerWrapperFactory.create(config.isForMutationInjectionFramework, this.javaClass, config.outputFileName)

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.infoStart()

        val codeBlocks: List<CodeBlock> = emptyList()
        val partitionSize = (codeBlocks.size + config.partitionNum - 1) / config.partitionNum
        val filtrationPhase = NGramBasedFiltration(config.filteringThreshold, codeBlocks)

        File(CLONE_PAIR_FILE_NAME).bufferedWriter().use { bw ->
            repeat(config.partitionNum) { i ->
                val startIndex: Int = i * partitionSize

                val invertedIndex =
                    InvertedIndex.create(partitionSize, codeBlocks, startIndex)
                logger.infoInvertedIndexCreationCompletion(i + 1)

                val locationPhase = NGramBasedLocation(invertedIndex)
                val cloneDetection =
                    CloneDetection(locationPhase, filtrationPhase, codeBlocks)
                Flowable.range(startIndex + 1, codeBlocks.size - startIndex - 1)
                    .parallelIfSpecified(config.threads)
                    .runOn(Schedulers.computation())
                    .flatMap { cloneDetection.exec(it) }
                    .sequential()
                    .blockingSubscribe { bw.appendLine("${it.first},${it.second}") }
                logger.infoCloneDetectionCompletion(i + 1)
            }
        }
        val endTime = System.currentTimeMillis()
        logger.infoEnd((endTime - startTime).toTime())
    }

    private fun input(config: NinjaConfig): List<CodeBlock> =
        config.src.bufferedReader().use { br ->
            sequence {
                while (true) {
                    val line = br.readLine() ?: break
                    if (line[0] == '#') {
                        continue
                    }
                    val info = line.split(",")
                    if (info[4].toInt() < config.minLine) {
                        while (br.readLine()[0] != '>'); // Skip
                        continue
                    }

                    val fileId = info[1].toLong()
                    val startLine = info[2].toInt()
                    val endLine = info[3].toInt()
                    val elements = mutableListOf<ElementFrequency>()
                    while (true) {
                        val elementLine = br.readLine()
                        if (elementLine[0] == '>') {
                            break
                        }
                        val (freq, element) = elementLine.split(":", limit = 2)
                        elements.add(element.hashCode() to freq.trim().toInt())
                    }
                    yield(CodeBlock(fileId, startLine, endLine, elements.sumBy { it.second }, elements))
                }
            }.toList()
        }
}

fun main(args: Array<String>) {
    val config: NinjaConfig = parseArgs(args)
    NinjaMain(config).run()
}
