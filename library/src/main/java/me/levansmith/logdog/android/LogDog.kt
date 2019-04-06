package me.levansmith.logdog.android

import me.levansmith.logging.LogProvider


public object LogDog : AndroidLogger(), AndroidOptions<LogDog.OptionOwner> {

    /** Helper class for creating log tags easily */
    public object Tag {
        /** Create a simple, compact String representation of the given class */
        public fun create(c: Class<*>): String = c.simpleName
    }

    // Transfer ownership to the OptionOwner
    override fun newOptions(with: AndroidModifiers.() -> Unit) = OptionOwner(with)

    // It is mandatory that this be null within the parent context.
    // When the modifiers are null, it will force a new creation of modifiers
    override var modifiers: AndroidModifiers? = null

    // Transfer ownership to the OptionOwner
    override fun getLogger() = OptionOwner()

    // Create new modifiers
    override fun withModifiers(level: LogProvider.Level?): AndroidModifiers {
        return AndroidModifiers().apply { logLevel = level ?: AndroidLogProvider.VERBOSE }
    }


    /** Restricts options' scope to a singular outcome/invocation without leaking to other invocations on the same line. */
    public class OptionOwner internal constructor(with: AndroidModifiers.() -> Unit = {}) : AndroidLogger(), AndroidOptions<OptionOwner> {

        // It is mandatory that this be initialized within the OptionOwner
        override var modifiers: AndroidModifiers? = AndroidModifiers().apply(with)

        // The OptionOwner is always the source of truth
        override fun getLogger() = this

        // Shouldn't have to be used since we can't transfer ownership from here, so just return this
        override fun newOptions(with: AndroidModifiers.() -> Unit) = this

        // Important! As opposed to the parent level, the modifiers within the OptionOwner should be used
        override fun withModifiers(level: LogProvider.Level?): AndroidModifiers? {
            if (level == null) return modifiers
            return modifiers.apply { this!!.logLevel = level }
        }
    }
}

