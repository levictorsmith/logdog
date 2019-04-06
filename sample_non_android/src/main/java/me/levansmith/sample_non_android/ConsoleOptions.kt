package me.levansmith.sample_non_android

import me.levansmith.logging.dispatch.Dispatcher
import me.levansmith.logging.Option
import me.levansmith.logging.dispatch.Modifiers

interface ConsoleOptions<L : Dispatcher<Modifiers>> : Option<Modifiers, L> {
    val g: L
        get() = with { logLevel = ConsoleLogProvider.GOOD }
    val b: L
        get() = with { logLevel = ConsoleLogProvider.BAD }
}