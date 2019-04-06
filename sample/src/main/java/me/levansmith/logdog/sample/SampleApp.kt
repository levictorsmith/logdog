package me.levansmith.logdog.sample

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import me.levansmith.logdog.android.AndroidLogProvider
import me.levansmith.logging.LogDogConfig

class SampleApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        // Whether you modify the config singleton directly or through methods or the apply method, any way will work

        // Directly
//        LogDogConfig.logProvider = AndroidLogProvider()
//        LogDogConfig.tag = "CUSTOM_GLOBAL_TAG"
//        LogDogConfig.boxPadding = 2
//        LogDogConfig.indentation = 2
//        LogDogConfig.autoTag = true
//        LogDogConfig.disableLogs = !BuildConfig.DEBUG
//        LogDogConfig.logThreshold = Log.VERBOSE
//        LogDogConfig.analyticsHandler = {
//            FirebaseAnalytics.getInstance(this).logEvent(it.eventName, it.toBundle())
//        }

        // With apply
//        LogDogConfig.apply {
//            logProvider = AndroidLogProvider()
//            tag = "CUSTOM_GLOBAL_TAG"
//            boxPadding = 2
//            indentation = 2
//            autoTag = true
//            disableLogs = !BuildConfig.DEBUG
//            logThreshold = Log.VERBOSE
//            analyticsHandler = {
//                FirebaseAnalytics.getInstance(this).logEvent(it.eventName, it.toBundle())
//            }
//        }

        // With direct methods
        LogDogConfig
            .logProvider(AndroidLogProvider())
            .tag("CUSTOM_GLOBAL_TAG")
            .boxPadding(2)
            .indentation(2)
            .autoTag(true)
            .disableLogs(!BuildConfig.DEBUG)
            .logThreshold(Log.VERBOSE)
            .analyticsHandler {
                FirebaseAnalytics.getInstance(this).logEvent(it.eventName, it.mapTo(Bundle()) { key, value ->
                    putString(key, value)
                })
            }
    }
}