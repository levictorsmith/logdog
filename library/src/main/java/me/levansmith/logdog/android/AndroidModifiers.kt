package me.levansmith.logdog.android

import me.levansmith.logging.dispatch.Modifiers

data class AndroidModifiers(
    var showToast: Boolean = false,
    var sendBroadcast: Boolean = false
) : Modifiers(AndroidLogProvider.VERBOSE)