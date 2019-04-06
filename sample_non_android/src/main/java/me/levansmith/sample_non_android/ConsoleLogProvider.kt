package me.levansmith.sample_non_android

import me.levansmith.logging.LogLevelException
import me.levansmith.logging.LogProvider

class ConsoleLogProvider : LogProvider {
    object GOOD : LogProvider.Level() {
        override val level: Int = 1
    }
    object BAD : LogProvider.Level() {
        override val level: Int = 2
    }

    override val max: LogProvider.Level = BAD
    override val min: LogProvider.Level = GOOD

    override fun map(level: Int) = when(level) {
        1 -> GOOD
        2 -> BAD
        else -> throw LogLevelException()
    }

    override fun logByLevel(level: LogProvider.Level?, tag: String?, message: String?, error: Throwable?): Int {
        val errorString = if (error != null) "\n\t$error" else ""
        return when(level) {
            is GOOD -> {
                println("$tag: $message$errorString")
                0
            }
            is BAD -> {
                System.err.println("$tag: $message$errorString")
                1
            }
            else -> throw LogLevelException()
        }
    }
}
