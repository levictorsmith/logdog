package me.levansmith.logdog.sample

import android.app.Application

class SampleApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        // Whether you modify the config singleton directly or through a modifier, either way will work
//        LogDogConfig.apply {
//            tag = "CUSTOM_GLOBAL_TAG"
//            boxPadding = 2
//        }
//        LogDogConfig.Modifier()
//            .tag("2ND_GLOBAL_TAG")
//            .boxPadding(2)
    }
}