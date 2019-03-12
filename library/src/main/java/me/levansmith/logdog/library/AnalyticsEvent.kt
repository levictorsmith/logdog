package me.levansmith.logdog.library

import android.os.Bundle
import org.json.JSONObject

abstract class AnalyticsEvent(val eventName: String) {

    abstract fun getFields(): Map<String, String>

    fun toBundle(): Bundle {
        return Bundle().apply {
            getFields().forEach {
                putString(it.key, it.value)
            }
        }
    }
    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            getFields().forEach {
                put(it.key, it.value)
            }
        }
    }
    fun toMap(): MutableMap<String, String> {
        return getFields().toMutableMap()
    }
}