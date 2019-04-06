package me.levansmith.logdog.android

import me.levansmith.logging.Dispatcher

data class AndroidModifiers(
    var showToast: Boolean = false,
    var sendBroadcast: Boolean = false
) : Dispatcher.Modifiers(AndroidLogProvider.VERBOSE)