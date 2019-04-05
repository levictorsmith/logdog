package me.levansmith.logdog.library


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
                field = if (value >= logProvider!!.min && value <= logProvider!!.max) value else throw IllegalArgumentException("Inappropriate log level")
            }
        }
    var analyticsHandler: (event: AnalyticsEvent) -> Unit = {}

    fun logProvider(logProvider: LogProvider): LogDogConfig {
        this.logProvider = logProvider
        return this
    }

    fun tag(tag: String): LogDogConfig {
        this.tag = tag
        return this
    }
    fun tagGenerator(generator: () -> String): LogDogConfig {
        this.tagGenerator = generator
        return this
    }
    fun boxPadding(padding: Int): LogDogConfig {
        this.boxPadding = padding
        return this
    }
    fun indentation(indentation: Int): LogDogConfig {
        this.indentation = indentation
        return this
    }
    fun autoTag(flag: Boolean): LogDogConfig {
        this.autoTag = flag
        return this
    }
    fun disableLogs(flag: Boolean): LogDogConfig {
        this.disableLogs = flag
        return this
    }
    fun logThreshold(logLevel: LogProvider.Level): LogDogConfig {
        this.logThreshold = setLogThreshold(logLevel)
        return this
    }
    fun logThreshold(logLevel: Int): LogDogConfig {
        this.logThreshold = setLogThreshold(logLevel)
        return this
    }
    fun analyticsHandler(block: (AnalyticsEvent) -> Unit): LogDogConfig {
        this.analyticsHandler = block
        return this
    }

    private fun <T> setLogThreshold(value: T): LogProvider.Level {
        return if (value is Int || value !is Int) {
            logProvider!!.map(value as Int)
        } else value as LogProvider.Level
    }
}