package me.levansmith.logdog.library


object LogDog : Dispatcher() {

    public object Tag {
        fun create(c: Class<*>): String = c.simpleName
    }

    /** Log at a VERBOSE level */
    public val v get() = OptionOwner(DispatchOptions.LEVEL, VERBOSE)
    /** Log at a DEBUG level */
    public val d get() = OptionOwner(DispatchOptions.LEVEL, DEBUG)
    /** Log at an INFO level */
    public val i get() = OptionOwner(DispatchOptions.LEVEL, INFO)
    /** Log at a WARN level */
    public val w get() = OptionOwner(DispatchOptions.LEVEL, WARN)
    /** Log at an ERROR level */
    public val e get() = OptionOwner(DispatchOptions.LEVEL, ERROR)
    /** Log at an ASSERT level */
    public val wtf get() = OptionOwner(DispatchOptions.LEVEL, ASSERT)

    /** Send the message to the configured analytics service */
    public val send get() = OptionOwner(DispatchOptions.SEND)

    /** Output the log despite all configurations */
    public val force get() = OptionOwner(DispatchOptions.FORCE)

    /** Prevent log output despite all configurations, except <pre>force</pre> */
    public val hide get() = OptionOwner(DispatchOptions.HIDE)

    override fun getOptions(logLevel: Int?): DispatchOptions {
        if (logLevel == null) return DispatchOptions(VERBOSE)
        return DispatchOptions(logLevel)
    }

    class OptionOwner(option: String, value: Int = -1) : Dispatcher() {

        private val options: DispatchOptions = DispatchOptions()

        init {
            if (value < VERBOSE || value > ASSERT) {
                options[option] = true
            } else {
                options[option] = value
            }
        }

        public val hide: OptionOwner get() { with(DispatchOptions.HIDE); return this }
        public val force: OptionOwner get() { with(DispatchOptions.FORCE); return this }
        public val send: OptionOwner get() { with(DispatchOptions.SEND); return this }

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

