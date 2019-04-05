package me.levansmith.logdog.library

import me.levansmith.logdog.library.android.AndroidLog
import me.levansmith.logdog.library.android.AndroidLogger
import me.levansmith.logdog.library.android.AndroidModifiers


public object LogDog : AndroidLogger() {

    /** Helper class for creating log tags easily */
    public object Tag {
        /** Create a simple, compact String representation of the given class */
        public fun create(c: Class<*>): String = c.simpleName
    }

    /** Log at a VERBOSE level */
    public val v: Option get() = Option { logLevel = AndroidLog.VERBOSE }
    /** Log at a DEBUG level */
    public val d: Option get() = Option { logLevel = AndroidLog.DEBUG }
    /** Log at an INFO level */
    public val i: Option get() = Option { logLevel = AndroidLog.INFO }
    /** Log at a WARN level */
    public val w: Option get() = Option { logLevel = AndroidLog.WARN }
    /** Log at an ERROR level */
    public val e: Option get() = Option { logLevel = AndroidLog.ERROR }
    /** Log at an ASSERT level */
    public val wtf: Option get() = Option { logLevel = AndroidLog.ASSERT }

    /** Send the message to the configured analytics service */
    public val send: Option get() = Option { willSend = true }

    /** Output the log despite all configurations */
    public val force: Option get() = Option { willForce = true }

    /** Prevent log output despite all configurations, except <pre>force</pre> */
    public val hide: Option get() = Option { willHide = true }

    /** Show thread info */
    public val showThread: Option get() = Option { showThreadInfo = true }

    /** Show a Toast message with the particular log message */
    public val showToast: Option get() = Option { showToast = true }

    /** Send an Android broadcast with the logged info as an extra */
    public val sendBroadcast: Option get() = Option { sendBroadcast = true }

    public fun <T : Any> extra(key: String, value: T) = Option { extras[key] = value }

    override fun withModifiers(level: LogProvider.Level?): AndroidModifiers {
        return AndroidModifiers().apply { logLevel = level ?: AndroidLog.VERBOSE }
    }


    /** Restricts options' scope to a singular outcome/invocation without leaking to other invocations on the same line. */
    public class Option internal constructor(with: AndroidModifiers.() -> Unit = {}) : AndroidLogger() {

        private var modifiers: AndroidModifiers = AndroidModifiers().apply(with)

        /** Send the message to the configured analytics service */
        public val send: Option get() = with { willSend = true }
        /** Output the log despite all configurations */
        public val force: Option get() = with { willForce = true }
        /** Prevent log output despite all configurations, except <pre>force</pre> */
        public val hide: Option get() = with { willHide = true }
        /** Show thread info */
        public val showThread: Option get() = with { showThreadInfo = true }

        /** Show a Toast message with the particular log message */
        public val showToast: Option get() = with { showToast = true }
        /** Send an Android broadcast with the logged info as an extra */
        public val sendBroadcast: Option get() = with { sendBroadcast = true }

        /** Add specific extra parameters for options or usage later on down the line */
        public fun <T : Any> extra(key: String, value: T) = with { extras[key] = value }

        /** Log at a VERBOSE level */
        public val v: Option get() = with { logLevel = AndroidLog.VERBOSE }
        /** Log at a DEBUG level */
        public val d: Option get() = with { logLevel = AndroidLog.DEBUG }
        /** Log at an INFO level */
        public val i: Option get() = with { logLevel = AndroidLog.INFO }
        /** Log at a WARN level */
        public val w: Option get() = with { logLevel = AndroidLog.WARN }
        /** Log at an ERROR level */
        public val e: Option get() = with { logLevel = AndroidLog.ERROR }
        /** Log at an ASSERT level */
        public val wtf: Option get() = with { logLevel = AndroidLog.ASSERT }

        override fun withModifiers(level: LogProvider.Level?): AndroidModifiers {
            if (level == null) return modifiers
            return modifiers.apply { logLevel = level }
        }

        private fun with(with: AndroidModifiers.() -> Unit): Option {
            modifiers = modifiers.apply(with)
            return this
        }
    }
}

