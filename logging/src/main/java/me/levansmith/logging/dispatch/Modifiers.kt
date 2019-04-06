package me.levansmith.logging.dispatch

import me.levansmith.logging.LogProvider

open class Modifiers(
    var logLevel: LogProvider.Level?,
    var willForce: Boolean = false,
    var willHide: Boolean = false,
    var willSend: Boolean = false,
    var showThreadInfo: Boolean = false,
    val extras: MutableMap<String, Any> = mutableMapOf()
)
