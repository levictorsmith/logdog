package me.levansmith.logging


interface LogProvider {
    abstract class Level {
        abstract val level: Int
        operator fun compareTo(level: Level): Int {
            return this.level.compareTo(level.level)
        }
    }

    val max: Level
    val min: Level

    fun map(level: Int): Level
    fun logByLevel(level: Level?, tag: String?, message: String?, error: Throwable?): Int
}

