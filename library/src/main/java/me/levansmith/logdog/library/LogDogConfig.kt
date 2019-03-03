package me.levansmith.logdog.library

import android.util.Log

object LogDogConfig {
    var tag: String = ""
    var showThreadInfo: Boolean = false
    var boxPadding: Int = 2
    var indentation: Int = 2
    var autoTag: Boolean = true
    var disableLogs: Boolean = false
    var logThreshold: Int = Log.VERBOSE
        set(value) {
            field = if (value >= Log.VERBOSE && value <= Log.ASSERT) value else throw Exception("Inappropriate log level")
        }

    fun tag(tag: String): LogDogConfig {
        this.tag = tag
        return this
    }
    fun showThreadInfo(flag: Boolean): LogDogConfig {
        this.showThreadInfo = flag
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
    fun logThreshold(logLevel: Int): LogDogConfig {
        this.logThreshold = logLevel
        return this
    }
}