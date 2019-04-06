package me.levansmith.sample_non_android

import me.levansmith.logging.DispatchLogger
import me.levansmith.logging.LogProvider
import me.levansmith.logging.dispatch.Modifiers

object Logger : DispatchLogger<Modifiers>(ConsoleLogProvider()), ConsoleOptions<Logger.OptionOwner> {

    // Must be null
    override var modifiers: Modifiers? = null

    override fun newOptions(with: Modifiers.() -> Unit) = OptionOwner(with)

    override fun getLogger() = OptionOwner()

    override val className: String = Logger::class.java.name

    override fun withModifiers(level: LogProvider.Level?): Modifiers {
        // If you don't want extra modifiers, you don't have to
        return Modifiers(ConsoleLogProvider.GOOD)
    }
    /** Restricts options' scope to a singular outcome/invocation without leaking to other invocations on the same line. */
    public class OptionOwner internal constructor(with: Modifiers.() -> Unit = {}) : DispatchLogger<Modifiers>(
        ConsoleLogProvider()
    ), ConsoleOptions<OptionOwner> {

        override val className: String = OptionOwner::class.java.name
        override var modifiers: Modifiers? = Modifiers(ConsoleLogProvider.GOOD).apply(with)

        override fun newOptions(with: Modifiers.() -> Unit) = this

        override fun getLogger() = this

        override fun withModifiers(level: LogProvider.Level?): Modifiers? {
            if (level == null) return modifiers
            return modifiers.apply { this!!.logLevel = level }
        }

    }
}
