package me.levansmith.logdog.library.android

import android.content.Context
import android.content.Intent
import android.widget.Toast
import me.levansmith.logdog.library.AnalyticsEvent
import me.levansmith.logdog.library.DispatchLogger

abstract class AndroidLogger : DispatchLogger(AndroidLog()) {

    companion object {
        // For convenience
        private val VERBOSE = AndroidLog.VERBOSE
        private val DEBUG = AndroidLog.DEBUG
        private val INFO = AndroidLog.INFO
        private val WARN = AndroidLog.WARN
        private val ERROR = AndroidLog.ERROR
        private val ASSERT = AndroidLog.ASSERT
        public const val EXTRA_KEY_CONTEXT = "me.levansmith.logdog.library.android.AndroidLogger.EXTRA_KEY_CONTEXT"
        public const val EXTRA_KEY_INTENT = "me.levansmith.logdog.library.android.AndroidLogger.EXTRA_KEY_INTENT"
        public const val EXTRA_DELEGATE = "me.levansmith.logdog.library.android.AndroidLogger.EXTRA_DELEGATE"
    }

    // Verbose
    public fun v(tag: String, msg: String) = logByLevel(VERBOSE, tag, msg)
    public fun v(tag: String, format: String, vararg args: Any): Int = logByLevel(VERBOSE, tag, format, args)
    public fun v(tag: String, msg: String, tr: Throwable): Int = logByLevel(VERBOSE, tag, msg, tr)
    public fun v(tag: String, format: String, tr: Throwable, vararg args: Any): Int = logByLevel(VERBOSE, tag, format, tr, args)
    public fun v(msg: String): Int = logByLevel(VERBOSE, msg)
    public fun v(format: String, vararg args: Any): Int = logByLevel(VERBOSE, format, args)
    public fun v(msg: String, tr: Throwable): Int = logByLevel(VERBOSE, msg, tr)
    public fun v(format: String, tr: Throwable, vararg args: Any): Int = logByLevel(VERBOSE, format, tr, args)
    public fun v(tag: String, event: AnalyticsEvent): Int = logByLevel(VERBOSE, tag, event)
    public fun v(event: AnalyticsEvent): Int = logByLevel(VERBOSE, event)

    // Debug
    public fun d(tag: String, msg: String) = logByLevel(DEBUG, tag, msg)
    public fun d(tag: String, format: String, vararg args: Any): Int = logByLevel(DEBUG, tag, format, args)
    public fun d(tag: String, msg: String, tr: Throwable): Int = logByLevel(DEBUG, tag, msg, tr)
    public fun d(tag: String, format: String, tr: Throwable, vararg args: Any): Int = logByLevel(DEBUG, tag, format, tr, args)
    public fun d(msg: String): Int = logByLevel(DEBUG, msg)
    public fun d(format: String, vararg args: Any): Int = logByLevel(DEBUG, format, args)
    public fun d(msg: String, tr: Throwable): Int = logByLevel(DEBUG, msg, tr)
    public fun d(format: String, tr: Throwable, vararg args: Any): Int = logByLevel(DEBUG, format, tr, args)
    public fun d(tag: String, event: AnalyticsEvent): Int = logByLevel(DEBUG, tag, event)
    public fun d(event: AnalyticsEvent): Int = logByLevel(DEBUG, event)

    // Info
    public fun i(tag: String, msg: String) = logByLevel(INFO, tag, msg)
    public fun i(tag: String, format: String, vararg args: Any): Int = logByLevel(INFO, tag, format, args)
    public fun i(tag: String, msg: String, tr: Throwable): Int = logByLevel(INFO, tag, msg, tr)
    public fun i(tag: String, format: String, tr: Throwable, vararg args: Any): Int = logByLevel(INFO, tag, format, tr, args)
    public fun i(msg: String): Int = logByLevel(INFO, msg)
    public fun i(format: String, vararg args: Any): Int = logByLevel(INFO, format, args)
    public fun i(msg: String, tr: Throwable): Int = logByLevel(INFO, msg, tr)
    public fun i(format: String, tr: Throwable, vararg args: Any): Int = logByLevel(INFO, format, tr, args)
    public fun i(tag: String, event: AnalyticsEvent): Int = logByLevel(INFO, tag, event)
    public fun i(event: AnalyticsEvent): Int = logByLevel(INFO, event)

    // Warn
    public fun w(tag: String, msg: String) = logByLevel(WARN, tag, msg)
    public fun w(tag: String, format: String, vararg args: Any): Int = logByLevel(WARN, tag, format, args)
    public fun w(tag: String, msg: String, tr: Throwable): Int = logByLevel(WARN, tag, msg, tr)
    public fun w(tag: String, format: String, tr: Throwable, vararg args: Any): Int = logByLevel(WARN, tag, format, tr, args)
    public fun w(msg: String): Int = logByLevel(WARN, msg)
    public fun w(format: String, vararg args: Any): Int = logByLevel(WARN, format, args)
    public fun w(msg: String, tr: Throwable): Int = logByLevel(WARN, msg, tr)
    public fun w(format: String, tr: Throwable, vararg args: Any): Int = logByLevel(WARN, format, tr, args)
    public fun w(tag: String, event: AnalyticsEvent): Int = logByLevel(WARN, tag, event)
    public fun w(event: AnalyticsEvent): Int = logByLevel(WARN, event)

    // Error
    public fun e(tag: String, msg: String) = logByLevel(ERROR, tag, msg)
    public fun e(tag: String, format: String, vararg args: Any): Int = logByLevel(ERROR, tag, format, args)
    public fun e(tag: String, msg: String, tr: Throwable): Int = logByLevel(ERROR, tag, msg, tr)
    public fun e(tag: String, format: String, tr: Throwable, vararg args: Any): Int = logByLevel(ERROR, tag, format, tr, args)
    public fun e(msg: String): Int = logByLevel(ERROR, msg)
    public fun e(format: String, vararg args: Any): Int = logByLevel(ERROR, format, args)
    public fun e(msg: String, tr: Throwable): Int = logByLevel(ERROR, msg, tr)
    public fun e(format: String, tr: Throwable, vararg args: Any): Int = logByLevel(ERROR, format, tr, args)
    public fun e(tag: String, event: AnalyticsEvent): Int = logByLevel(ERROR, tag, event)
    public fun e(event: AnalyticsEvent): Int = logByLevel(ERROR, event)

    // What-a-terrible-failure
    public fun wtf(tag: String, msg: String) = logByLevel(ASSERT, tag, msg)
    public fun wtf(tag: String, format: String, vararg args: Any): Int = logByLevel(ASSERT, tag, format, args)
    public fun wtf(tag: String, msg: String, tr: Throwable): Int = logByLevel(ASSERT, tag, msg, tr)
    public fun wtf(tag: String, format: String, tr: Throwable, vararg args: Any): Int = logByLevel(ASSERT, tag, format, tr, args)
    public fun wtf(msg: String): Int = logByLevel(ASSERT, msg)
    public fun wtf(format: String, vararg args: Any): Int = logByLevel(ASSERT, format, args)
    public fun wtf(msg: String, tr: Throwable): Int = logByLevel(ASSERT, msg, tr)
    public fun wtf(format: String, tr: Throwable, vararg args: Any): Int = logByLevel(ASSERT, format, tr, args)
    public fun wtf(tag: String, event: AnalyticsEvent): Int = logByLevel(ASSERT, tag, event)
    public fun wtf(event: AnalyticsEvent): Int = logByLevel(ASSERT, event)

    override fun preDispatch(modifiers: Modifiers, delegate: Delegate) {
        super.preDispatch(modifiers, delegate)
        val androidModifiers = (modifiers as AndroidModifiers)
        if (androidModifiers.sendBroadcast) {
            try {
                val context = androidModifiers.extras[EXTRA_KEY_CONTEXT] as Context?
                val intent = androidModifiers.extras[EXTRA_KEY_INTENT] as Intent?
                sendBroadcast(context!!, intent!!, delegate)
            } catch (e: Exception) {
                throw IllegalArgumentException("No Context or Intent provided. Add the context and intent as an extra to the Log call.")
            }
        }
        if (androidModifiers.showToast) {
            try {
                val context = androidModifiers.extras[EXTRA_KEY_CONTEXT] as Context?
                showToast(context!!, delegate)
            } catch (e: Exception) {
                throw IllegalArgumentException("No Context provided. Add the context as an extra to the Log call.")
            }
        }
    }

    private fun sendBroadcast(context: Context, intent: Intent, delegate: Delegate) {
        intent.putExtra(EXTRA_DELEGATE, delegate)
        context.sendBroadcast(intent)
    }

    private fun showToast(context: Context, delegate: Delegate) {
        Toast.makeText(context, delegate.message, Toast.LENGTH_LONG).show()
    }
}