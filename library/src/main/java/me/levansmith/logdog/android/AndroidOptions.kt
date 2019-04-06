package me.levansmith.logdog.android

import me.levansmith.logging.Dispatcher
import me.levansmith.logging.Option

interface AndroidOptions<out L : Dispatcher<AndroidModifiers>> : Option<AndroidModifiers, L> {
    /** Log at a VERBOSE level */
    val v: L
        get() = with { logLevel = AndroidLogProvider.VERBOSE }
    /** Log at a DEBUG level */
    val d: L
        get() = with { logLevel = AndroidLogProvider.DEBUG }
    /** Log at an INFO level */
    val i: L
        get() = with { logLevel = AndroidLogProvider.INFO }
    /** Log at a WARN level */
    val w: L
        get() = with { logLevel = AndroidLogProvider.WARN }
    /** Log at an ERROR level */
    val e: L
        get() = with { logLevel = AndroidLogProvider.ERROR }
    /** Log at an ASSERT level */
    val wtf: L
        get() = with { logLevel = AndroidLogProvider.ASSERT }

    /** Show a Toast message with the particular log message */
    val showToast: L
        get() = with { showToast = true }

    /** Send an Android broadcast with the logged info as an extra */
    val sendBroadcast: L
        get() = with { sendBroadcast = true }

}