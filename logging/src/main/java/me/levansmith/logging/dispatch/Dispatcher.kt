package me.levansmith.logging.dispatch

import me.levansmith.logging.AnalyticsEvent
import me.levansmith.logging.LogProvider
import java.io.Serializable
import java.util.Collections.emptyList

abstract class Dispatcher<M : Modifiers> {

    data class Delegate(
        var tag: String,
        var message: String = "",
        var format: String? = null,
        var error: Throwable? = null,
        var event: AnalyticsEvent? = null,
        var args: List<Any> = emptyList()
    ) : Serializable

    abstract fun withModifiers(level: LogProvider.Level?): M?

    abstract fun shouldDispatch(modifiers: M, delegate: Delegate): Boolean

    abstract fun preDispatch(modifiers: M, delegate: Delegate)

    abstract fun doDispatch(modifiers: M, delegate: Delegate): Int

    abstract fun postDispatch(modifiers: M, delegate: Delegate)

    fun dispatch(level: LogProvider.Level?, delegate: Delegate): Int {
        val modifiers = withModifiers(level)
        if (!shouldDispatch(modifiers!!, delegate)) return 0
        preDispatch(modifiers, delegate)
        val result = doDispatch(modifiers, delegate)
        postDispatch(modifiers, delegate)
        return result
    }
}