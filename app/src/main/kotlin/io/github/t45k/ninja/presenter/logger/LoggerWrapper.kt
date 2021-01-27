package io.github.t45k.ninja.presenter.logger

interface LoggerWrapper {
    fun infoStart()
    fun infoPreprocessCompletion(size: Int)
    fun infoInvertedIndexCreationCompletion(partition: Int)
    fun infoCloneDetectionCompletion(partition: Int)
    fun infoEnd(time: String)
}
