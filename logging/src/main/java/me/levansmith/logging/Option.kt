package me.levansmith.logging


interface Option<M : Dispatcher.Modifiers, out L : Dispatcher<M>> {
    var modifiers: M?
    fun newOptions(with: M.() -> Unit): L
    fun getLogger(): L

    /** Send the message to the configured analytics service */
    val send: L get() = with { willSend = true }
    /** Output the log despite all configurations */
    val force: L get() = with { willForce = true }
    /** Prevent log output despite all configurations, except <pre>force</pre> */
    val hide: L get() = with { willHide = true }
    /** Show thread info */
    val showThread: L get() = with { showThreadInfo = true }

    /** Add specific extra parameters for options or usage later on down the line */
    fun <T : Any> extra(key: String, value: T) = with { extras[key] = value }

    fun with(with: M.() -> Unit): L  {
        if (modifiers == null) {
            return newOptions(with)
        }
        modifiers = modifiers!!.apply(with)
        return getLogger()
    }
}
