package me.levansmith.logdog.library.android

import me.levansmith.logdog.library.Dispatcher

data class AndroidModifiers(
    var showToast: Boolean = false,
    var sendBroadcast: Boolean = false
) : Dispatcher.Modifiers(AndroidLog.VERBOSE)