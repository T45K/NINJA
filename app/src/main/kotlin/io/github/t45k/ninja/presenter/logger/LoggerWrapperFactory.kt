package io.github.t45k.ninja.presenter.logger

class LoggerWrapperFactory {
    companion object {
        fun create(isForMutationFramework: Boolean, clazz: Class<*>, outputFileName: String): LoggerWrapper =
            if (isForMutationFramework) {
                MFLoggerWrapper(outputFileName)
            } else {
                NormalLoggerWrapper(clazz)
            }
    }
}
