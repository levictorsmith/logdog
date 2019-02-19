package me.levansmith.logdog.library

object LogDogConfig {
    var tag: String = ""
    var showThreadInfo = false
    var boxPadding = 2
    var indentation = 2
    var autoTag = true

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
}