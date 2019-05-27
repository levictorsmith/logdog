package me.levansmith.logdog.android


/**
 * An intuitive Android logger with convenient functionality.
 */
class LogDog private constructor(with: AndroidModifiers.() -> Unit = {}) : AndroidLogger<LogDog>() {

    /**
     * Static-like options/methods devoid of any modifiers.
     * Any added modifiers will create a new LogDog with those modifiers.
     */
    companion object : AndroidLogger<LogDog>() {

        // The companion never has modifiers of its own
        override val modifiers: AndroidModifiers? = null

        // Create new modifiers
        override fun defaultModifiers() = AndroidModifiers()

        // Create a new logger to use
        override fun withLogger(with: AndroidModifiers.() -> Unit) = LogDog(with)
    }

    // Carry over the existing options
    override val modifiers: AndroidModifiers? = AndroidModifiers().apply(with)

    // Not used if logger already exists
    override fun defaultModifiers() = AndroidModifiers()

    // Use the existing logger so we can modify the existing modifiers
    override fun withLogger(with: AndroidModifiers.() -> Unit) = apply { with(modifiers!!) }
}
