package me.levansmith.logdog.android

import android.util.Log
import me.levansmith.logging.LogProvider

class AndroidLogProvider : LogProvider {

    override val min: LogProvider.Level = VERBOSE
    override val max: LogProvider.Level = ASSERT

    object VERBOSE : LogProvider.Level() {
        override val level: Int = Log.VERBOSE
    }

    object DEBUG : LogProvider.Level() {
        override val level: Int = Log.DEBUG
    }

    object INFO : LogProvider.Level() {
        override val level: Int = Log.INFO
    }

    object WARN : LogProvider.Level() {
        override val level: Int = Log.WARN
    }

    object ERROR : LogProvider.Level() {
        override val level: Int = Log.ERROR
    }

    object ASSERT : LogProvider.Level() {
        override val level: Int = Log.ASSERT
    }

    override fun map(level: Int): LogProvider.Level = when (level) {
        Log.VERBOSE -> VERBOSE
        Log.DEBUG -> DEBUG
        Log.INFO -> INFO
        Log.WARN -> WARN
        Log.ERROR -> ERROR
        Log.ASSERT -> ASSERT
        else -> throw Exception("Unknown log level")
    }

    override fun logByLevel(level: LogProvider.Level?, tag: String?, message: String?, error: Throwable?): Int {
        return when (level) {
            is VERBOSE -> if (error != null) Log.v(tag, message, error) else Log.v(tag, message)
            is DEBUG -> if (error != null) Log.d(tag, message, error) else Log.d(tag, message)
            is INFO -> if (error != null) Log.i(tag, message, error) else Log.i(tag, message)
            is WARN -> if (error != null) Log.w(tag, message, error) else Log.w(tag, message)
            is ERROR -> if (error != null) Log.e(tag, message, error) else Log.e(tag, message)
            is ASSERT -> if (error != null) Log.wtf(tag, message, error) else Log.wtf(tag, message)
            else -> throw Exception("Unknown log level")
        }
    }
}