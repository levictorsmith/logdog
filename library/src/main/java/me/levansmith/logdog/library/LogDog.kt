package me.levansmith.logdog.library


public object LogDog : Dispatcher() {

    /** Helper class for creating log tags easily */
    public object Tag {
        /** Create a simple, compact String representation of the given class */
        public fun create(c: Class<*>): String = c.simpleName
    }

    /** Log at a VERBOSE level */
    public val v: OptionOwner get() = OptionOwner(DispatchOptions.LEVEL, VERBOSE)
    /** Log at a DEBUG level */
    public val d: OptionOwner get() = OptionOwner(DispatchOptions.LEVEL, DEBUG)
    /** Log at an INFO level */
    public val i: OptionOwner get() = OptionOwner(DispatchOptions.LEVEL, INFO)
    /** Log at a WARN level */
    public val w: OptionOwner get() = OptionOwner(DispatchOptions.LEVEL, WARN)
    /** Log at an ERROR level */
    public val e: OptionOwner get() = OptionOwner(DispatchOptions.LEVEL, ERROR)
    /** Log at an ASSERT level */
    public val wtf: OptionOwner get() = OptionOwner(DispatchOptions.LEVEL, ASSERT)

    /** Send the message to the configured analytics service */
    public val send: OptionOwner get() = OptionOwner(DispatchOptions.SEND)

    /** Output the log despite all configurations */
    public val force: OptionOwner get() = OptionOwner(DispatchOptions.FORCE)

    /** Prevent log output despite all configurations, except <pre>force</pre> */
    public val hide: OptionOwner get() = OptionOwner(DispatchOptions.HIDE)

    override fun getOptions(logLevel: Int?): DispatchOptions {
        if (logLevel == null) return DispatchOptions(VERBOSE)
        return DispatchOptions(logLevel)
    }

    /** Restricts options' scope to a singular outcome/invocation without leaking to other invocations on the same line. */
    public class OptionOwner internal constructor(option: String, value: Int = -1) : Dispatcher() {

        private val options: DispatchOptions = DispatchOptions()

        init {
            if (value < VERBOSE || value > ASSERT) {
                options[option] = true
            } else {
                options[option] = value
            }
        }

        /** Send the message to the configured analytics service */
        public val send: OptionOwner get() { with(DispatchOptions.SEND); return this }
        /** Output the log despite all configurations */
        public val force: OptionOwner get() { with(DispatchOptions.FORCE); return this }
        /** Prevent log output despite all configurations, except <pre>force</pre> */
        public val hide: OptionOwner get() { with(DispatchOptions.HIDE); return this }

        /** Log at a VERBOSE level */
        public val v: OptionOwner get() { return with(DispatchOptions.LEVEL, VERBOSE) }
        /** Log at a DEBUG level */
        public val d: OptionOwner get() { return with(DispatchOptions.LEVEL, DEBUG) }
        /** Log at an INFO level */
        public val i: OptionOwner get() { return with(DispatchOptions.LEVEL, INFO) }
        /** Log at a WARN level */
        public val w: OptionOwner get() { return with(DispatchOptions.LEVEL, WARN) }
        /** Log at an ERROR level */
        public val e: OptionOwner get() { return with(DispatchOptions.LEVEL, ERROR) }
        /** Log at an ASSERT level */
        public val wtf: OptionOwner get() { return with(DispatchOptions.LEVEL, ASSERT) }

        override fun getOptions(logLevel: Int?): DispatchOptions {
            if (logLevel == null) return options
            return options.apply { this.logLevel = logLevel }
        }

        private fun with(option: String): OptionOwner {
            options[option] = true
            return this
        }
        private fun with(option: String, value: Int): OptionOwner {
            options[option] = value
            return this
        }
    }
}

