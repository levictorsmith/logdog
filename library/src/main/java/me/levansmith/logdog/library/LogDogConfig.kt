package me.levansmith.logdog.library

object LogDogConfig {
    var tag: String = ""
    var showThreadInfo = false
    var boxPadding = 2
    var indentation = 2

    class Modifier {
        fun tag(tag: String): Modifier {
            LogDogConfig.tag = tag
            return this
        }
        fun showThreadInfo(flag: Boolean): Modifier {
            LogDogConfig.showThreadInfo = flag
            return this
        }
        fun boxPadding(padding: Int): Modifier {
            LogDogConfig.boxPadding = padding
            return this
        }
        fun indentation(indentation: Int): Modifier {
            LogDogConfig.indentation = indentation
            return this
        }
    }
}