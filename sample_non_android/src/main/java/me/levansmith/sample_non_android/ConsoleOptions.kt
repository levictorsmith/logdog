package me.levansmith.sample_non_android

import me.levansmith.logging.Dispatcher
import me.levansmith.logging.Option

interface ConsoleOptions<L : Dispatcher<Dispatcher.Modifiers>> : Option<Dispatcher.Modifiers, L> {
    val g: L
        get() = with { logLevel = ConsoleLogProvider.GOOD }
    val b: L
        get() = with { logLevel = ConsoleLogProvider.BAD }
}