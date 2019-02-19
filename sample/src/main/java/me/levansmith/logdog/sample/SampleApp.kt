package me.levansmith.logdog.sample

import android.app.Application
import me.levansmith.logdog.library.LogDogConfig

class SampleApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        // Whether you modify the config singleton directly or through a modifier, either way will work
        LogDogConfig.apply {
            tag = "CUSTOM_GLOBAL_TAG"
            showThreadInfo = false
            boxPadding = 2
            indentation = 2
            autoTag = true
        }
        LogDogConfig
            .tag("2ND_GLOBAL_TAG")
            .showThreadInfo(false)
            .boxPadding(2)
            .indentation(2)
            .autoTag(true)
    }
}