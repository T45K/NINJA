package io.github.t45k.ninja

import io.github.t45k.ninja.entity.InvertedIndex
import io.github.t45k.ninja.entity.NGrams
import io.github.t45k.ninja.presenter.logger.LoggerWrapperFactory
import io.github.t45k.ninja.presenter.output.FormatFactory
import io.github.t45k.ninja.usecase.cloneDetection.CloneDetection
import io.github.t45k.ninja.usecase.cloneDetection.NGramBasedFiltration
import io.github.t45k.ninja.usecase.cloneDetection.NGramBasedLocation
import io.github.t45k.ninja.usecase.preprocess.JavaPreprocess
import io.github.t45k.ninja.util.parallelIfSpecified
import io.github.t45k.ninja.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class NinjaMain(private val config: NinjaConfig) {
    companion object {
        const val CODE_BLOCK_FILE_NAME = "code_blocks"
        const val CLONE_PAIR_FILE_NAME = "clone_pairs"
    }

    private val logger =
        LoggerWrapperFactory.create(config.isForMutationInjectionFramework, this.javaClass, config.outputFileName)

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.infoStart()

        val nGramsList: List<NGrams> = JavaPreprocess(config).collectNgramsList(config.src)
        logger.infoPreprocessCompletion(nGramsList.size)

        val partitionSize = (nGramsList.size + config.partitionNum - 1) / config.partitionNum
        val filtrationPhase = NGramBasedFiltration(config.filteringThreshold)

        File(CLONE_PAIR_FILE_NAME).bufferedWriter().use { bw ->
            repeat(config.partitionNum) { i ->
                val startIndex: Int = i * partitionSize

                val invertedIndex =
                    InvertedIndex.create(partitionSize, nGramsList, startIndex)
                logger.infoInvertedIndexCreationCompletion(i + 1)

                val locationPhase = NGramBasedLocation(invertedIndex)
                val cloneDetection =
                    CloneDetection(locationPhase, filtrationPhase, nGramsList)
                Flowable.range(startIndex + 1, nGramsList.size - startIndex - 1)
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

        FormatFactory.create(config.isForBigCloneEval)
            .convert(config.outputFileName)
    }
}

fun main(args: Array<String>) {
    val config: NinjaConfig = parseArgs(args)
    NinjaMain(config).run()
}
