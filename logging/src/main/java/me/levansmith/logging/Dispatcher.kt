package me.levansmith.logging

import java.io.Serializable
import java.util.Collections.emptyList

interface Dispatcher<M : Dispatcher.Modifiers> {
    open class Modifiers(
        var logLevel: LogProvider.Level?,
        var willForce: Boolean = false,
        var willHide: Boolean = false,
        var willSend: Boolean = false,
        var showThreadInfo: Boolean = false,
        val extras: MutableMap<String, Any> = mutableMapOf()
    )

    data class Delegate(
        var tag: String,
        var message: String = "",
        var format: String? = null,
        var error: Throwable? = null,
        var event: AnalyticsEvent? = null,
        var args: List<Any> = emptyList()
    ) : Serializable

    fun withModifiers(level: LogProvider.Level?): M?

    fun shouldDispatch(modifiers: M, delegate: Delegate): Boolean

    fun preDispatch(modifiers: M, delegate: Delegate)

    fun doDispatch(modifiers: M, delegate: Delegate): Int

    fun postDispatch(modifiers: M, delegate: Delegate)

    fun dispatch(level: LogProvider.Level?, delegate: Delegate): Int {
        val modifiers = withModifiers(level)
        if (!shouldDispatch(modifiers!!, delegate)) return 0
        preDispatch(modifiers, delegate)
        val result = doDispatch(modifiers, delegate)
        postDispatch(modifiers, delegate)
        return result
    }
}