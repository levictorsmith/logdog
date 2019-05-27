package me.levansmith.logdog.android

import android.content.Context
import android.content.Intent
import me.levansmith.logging.Option
import me.levansmith.logging.dispatch.Dispatcher

interface AndroidOptions<out L : Dispatcher<AndroidModifiers>> : Option<AndroidModifiers, L> {
    /** Log at a VERBOSE level */
    val v: L
        get() = withLogger { logLevel = AndroidLogProvider.VERBOSE }
    /** Log at a DEBUG level */
    val d: L
        get() = withLogger { logLevel = AndroidLogProvider.DEBUG }
    /** Log at an INFO level */
    val i: L
        get() = withLogger { logLevel = AndroidLogProvider.INFO }
    /** Log at a WARN level */
    val w: L
        get() = withLogger { logLevel = AndroidLogProvider.WARN }
    /** Log at an ERROR level */
    val e: L
        get() = withLogger { logLevel = AndroidLogProvider.ERROR }
    /** Log at an ASSERT level */
    val wtf: L
        get() = withLogger { logLevel = AndroidLogProvider.ASSERT }

    /** Show a Toast message with the particular log message */
    fun showToast(context: Context): L {
        return withLogger {
            showToast = true
            extras[AndroidLogger.EXTRA_KEY_CONTEXT] = context
        }
    }

    /** Send an Android broadcast with the logged info as an extra */
    fun sendBroadcast(context: Context, intent: Intent): L {
        return withLogger {
            sendBroadcast = true
            extras[AndroidLogger.EXTRA_KEY_CONTEXT] = context
            extras[AndroidLogger.EXTRA_KEY_INTENT] = intent
        }
    }

}