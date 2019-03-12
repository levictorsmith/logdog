package me.levansmith.logdog.sample

import android.app.Application
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import me.levansmith.logdog.library.LogDogConfig

class SampleApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        // Whether you modify the config singleton directly or through methods or the apply method, any way will work

        // Directly
//        LogDogConfig.tag = "CUSTOM_GLOBAL_TAG"
//        LogDogConfig.showThreadInfo = false
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
//            tag = "CUSTOM_GLOBAL_TAG"
//            showThreadInfo = false
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
            .tag("2ND_GLOBAL_TAG")
            .showThreadInfo(false)
            .boxPadding(2)
            .indentation(2)
            .autoTag(true)
            .disableLogs(!BuildConfig.DEBUG)
            .logThreshold(Log.VERBOSE)
            .analyticsHandler {
                FirebaseAnalytics.getInstance(this).logEvent(it.eventName, it.toBundle())
            }
    }
}