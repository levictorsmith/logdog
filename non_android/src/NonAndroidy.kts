package me.levansmith.sample_non_android

import me.levansmith.logdog.library.DispatchLogger
import me.levansmith.logdog.library.LogDogConfig
import me.levansmith.logdog.library.LogLevelException
import me.levansmith.logdog.library.LogProvider
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class NonAndroidLogProvider : LogProvider {
    object GOOD : LogProvider.Level() {
        override val level: Int = 1
    }
    object BAD : LogProvider.Level() {
        override val level: Int = 2
    }

    override val max: LogProvider.Level = BAD
    override val min: LogProvider.Level = GOOD

    override fun map(level: Int) = when(level) {
        1 -> GOOD
        2 -> BAD
        else -> throw LogLevelException()
    }

    override fun logByLevel(level: LogProvider.Level?, tag: String?, message: String?, error: Throwable?): Int {
        val errorString = if (error != null) "\n\t$error" else ""
        return when(level) {
            is GOOD -> {
                println("$tag: $message$errorString")
                0
            }
            is BAD -> {
                System.err.println("$tag: $message$errorString")
                1
            }
            else -> throw LogLevelException()
        }
    }
}

object Logger : DispatchLogger(NonAndroidLogProvider()) {

    public val g: Option
        get() = Option {
            logLevel = me.levansmith.sample_non_android.NonAndroidLogProvider.GOOD
        }
    public val b: Option
        get() = Option {
            logLevel = me.levansmith.sample_non_android.NonAndroidLogProvider.BAD
        }

    public val force: Option
        get() = Option {
            willForce = true
        }
    public val hide: Option
        get() = Option {
            willHide = true
        }
    public val showThread: Option
        get() = Option {
            showThreadInfo = true
        }
    public fun <T : Any> extra(key: String, value: T) =
        Option { extras[key] = value }

    override fun withModifiers(level: LogProvider.Level?): Modifiers {
        // If you don't want extra modifiers, you don't have to
        return Modifiers(NonAndroidLogProvider.GOOD)
    }
    /** Restricts options' scope to a singular outcome/invocation without leaking to other invocations on the same line. */
    public class Option internal constructor(with: Modifiers.() -> Unit = {}) : DispatchLogger(NonAndroidLogProvider()) {

        private var modifiers: Modifiers = Modifiers(NonAndroidLogProvider.GOOD).apply(with)

        public val force: Option get() = with { willForce = true }
        public val hide: Option get() = with { willHide = true }
        public val showThread: Option get() = with { showThreadInfo = true }
        public fun <T : Any> extra(key: String, value: T) = with { extras[key] = value }

        public val g: Option
            get() = with { logLevel = me.levansmith.sample_non_android.NonAndroidLogProvider.GOOD }
        public val b: Option
            get() = with { logLevel = me.levansmith.sample_non_android.NonAndroidLogProvider.BAD }

        override fun withModifiers(level: LogProvider.Level?): Modifiers {
            if (level == null) return modifiers
            return modifiers.apply { logLevel = level }
        }

        private fun with(with: Modifiers.() -> Unit): Option {
            modifiers = modifiers.apply(with)
            return this
        }
    }
}

fun main(args: Array<String>) {
    LogDogConfig.tagGenerator = {
        val formatter = DateTimeFormatter.ISO_INSTANT
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())
        "${formatter.format(Instant.now())}: GENERIC TAG"
    }
    Logger.g.log("This is a message")
    Logger.hide.log("Hidden message")
    Logger.force.hide.showThread.log("CUSTOM_TAG", "Have a cookie!")
    Logger.b.log("An error!", Exception("This is an error message!"))
}
