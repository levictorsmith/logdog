package me.levansmith.logdog.library

abstract class AnalyticsEvent(val eventName: String) {

    abstract fun getFields(): Map<String, String>

    fun <T> mapTo(initial: T, action: T.(String, String) -> Unit): T {
        return initial.apply {
            getFields().forEach {
                action.invoke(this, it.key, it.value)
            }
        }
    }

    fun toMap(): MutableMap<String, String> {
        return getFields().toMutableMap()
    }
}