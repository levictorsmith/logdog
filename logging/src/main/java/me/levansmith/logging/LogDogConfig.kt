package me.levansmith.logging


object LogDogConfig {
    var logProvider: LogProvider? = null
    var tag: String = ""
    var tagGenerator: (() -> String)? = null
    var boxPadding: Int = 2
    var indentation: Int = 2
    var autoTag: Boolean = true
    var disableLogs: Boolean = false
    var logThreshold: LogProvider.Level? = null
        set(value) {
            if (value != null) {
                field = if (value >= logProvider!!.min && value <= logProvider!!.max) value else throw LogLevelException()
            }
        }
    var analyticsHandler: (event: AnalyticsEvent) -> Unit = {}

    fun logProvider(logProvider: LogProvider): LogDogConfig {
        LogDogConfig.logProvider = logProvider
        return this
    }

    fun tag(tag: String): LogDogConfig {
        LogDogConfig.tag = tag
        return this
    }
    fun tagGenerator(generator: () -> String): LogDogConfig {
        tagGenerator = generator
        return this
    }
    fun boxPadding(padding: Int): LogDogConfig {
        boxPadding = padding
        return this
    }
    fun indentation(indentation: Int): LogDogConfig {
        LogDogConfig.indentation = indentation
        return this
    }
    fun autoTag(flag: Boolean): LogDogConfig {
        autoTag = flag
        return this
    }
    fun disableLogs(flag: Boolean): LogDogConfig {
        disableLogs = flag
        return this
    }
    fun logThreshold(logLevel: LogProvider.Level): LogDogConfig {
        logThreshold = setLogThreshold(logLevel)
        return this
    }
    fun logThreshold(logLevel: Int): LogDogConfig {
        logThreshold = setLogThreshold(logLevel)
        return this
    }
    fun analyticsHandler(block: (AnalyticsEvent) -> Unit): LogDogConfig {
        analyticsHandler = block
        return this
    }

    private fun <T> setLogThreshold(value: T): LogProvider.Level {
        return if (value is Int || value !is Int) {
            logProvider!!.map(value as Int)
        } else value as LogProvider.Level
    }
}