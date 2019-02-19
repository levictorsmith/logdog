package me.levansmith.logdog.library

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

object LogDog {

    private const val DEFAULT_COUNT_LABEL = "me.levansmith.logdog.library.LogDog.DEFAULT_COUNT_LABEL"
    private const val DEFAULT_TIME_LABEL = "me.levansmith.logdog.library.LogDog.DEFAULT_TIME_LABEL"

    private const val DEFAULT_TIME_FORMAT = "HH'h' mm'm' ss.SSS's'"

    private val counters: MutableMap<String, Long> = mutableMapOf()
    private val timers: MutableMap<String, TimerParams> = mutableMapOf()

    private val config = LogDogConfig

    // For convenience
    const val VERBOSE = Log.VERBOSE
    const val DEBUG = Log.DEBUG
    const val INFO = Log.INFO
    const val WARN = Log.WARN
    const val ERROR = Log.ERROR
    const val ASSERT = Log.ASSERT

    // Verbose
    fun v(tag: String, msg: String): Int = Log.v(tag, decorateMessage(msg))
    fun v(tag: String, format: String, vararg args: Any): Int = v(tag, format(format, *args))
    fun v(tag: String, msg: String, tr: Throwable): Int = Log.v(tag, decorateMessage(msg), tr)
    fun v(tag: String, format: String, tr: Throwable, vararg args: Any): Int = v(tag, format(format, *args), tr)

    fun v(msg: String): Int = Log.v(getTag(""), decorateMessage(msg))
    fun v(format: String, vararg args: Any): Int = v(getTag(""), format(format, *args))
    fun v(msg: String, tr: Throwable): Int = Log.v(getTag(""), decorateMessage(msg), tr)
    fun v(format: String, tr: Throwable, vararg args: Any): Int = v(getTag(""), format(format, *args), tr)

    // Debug
    fun d(tag: String, msg: String): Int = Log.d(tag, decorateMessage(msg))
    fun d(tag: String, format: String, vararg args: Any): Int = d(tag, format(format, *args))
    fun d(tag: String, msg: String, tr: Throwable): Int = Log.d(tag, decorateMessage(msg), tr)
    fun d(tag: String, format: String, tr: Throwable, vararg args: Any): Int = d(tag, format(format, *args), tr)

    fun d(msg: String): Int = Log.d(getTag(""), decorateMessage(msg))
    fun d(format: String, vararg args: Any): Int = d(getTag(""), format(format, *args))
    fun d(msg: String, tr: Throwable): Int = Log.d(getTag(""), decorateMessage(msg), tr)
    fun d(format: String, tr: Throwable, vararg args: Any): Int = d(getTag(""), format(format, *args), tr)

    // Info
    fun i(tag: String, msg: String): Int = Log.i(tag, decorateMessage(msg))
    fun i(tag: String, format: String, vararg args: Any): Int = i(tag, format(format, *args))
    fun i(tag: String, msg: String, tr: Throwable): Int = Log.i(tag, decorateMessage(msg), tr)
    fun i(tag: String, format: String, tr: Throwable, vararg args: Any): Int = i(tag, format(format, *args), tr)

    fun i(msg: String): Int = Log.i(getTag(""), decorateMessage(msg))
    fun i(format: String, vararg args: Any): Int = i(getTag(""), format(format, *args))
    fun i(msg: String, tr: Throwable): Int = Log.i(getTag(""), decorateMessage(msg), tr)
    fun i(format: String, tr: Throwable, vararg args: Any): Int = i(getTag(""), format(format, *args), tr)

    // Warn
    fun w(tag: String, msg: String): Int = Log.w(tag, decorateMessage(msg))
    fun w(tag: String, format: String, vararg args: Any): Int = w(tag, format(format, *args))
    fun w(tag: String, msg: String, tr: Throwable): Int = Log.w(tag, decorateMessage(msg), tr)
    fun w(tag: String, format: String, tr: Throwable, vararg args: Any): Int = w(tag, format(format, *args), tr)

    fun w(msg: String): Int = Log.w(getTag(""), decorateMessage(msg))
    fun w(format: String, vararg args: Any): Int = w(getTag(""), format(format, *args))
    fun w(msg: String, tr: Throwable): Int = Log.w(getTag(""), decorateMessage(msg), tr)
    fun w(format: String, tr: Throwable, vararg args: Any): Int = w(getTag(""), format(format, *args), tr)

    // Error
    fun e(tag: String, msg: String): Int = Log.e(tag, decorateMessage(msg))
    fun e(tag: String, format: String, vararg args: Any): Int = e(tag, format(format, *args))
    fun e(tag: String, msg: String, tr: Throwable): Int = Log.e(tag, decorateMessage(msg), tr)
    fun e(tag: String, format: String, tr: Throwable, vararg args: Any): Int = e(tag, format(format, *args), tr)

    fun e(msg: String): Int = Log.e(getTag(""), decorateMessage(msg))
    fun e(format: String, vararg args: Any): Int = e(getTag(""), format(format, *args))
    fun e(msg: String, tr: Throwable): Int = Log.e(getTag(""), decorateMessage(msg), tr)
    fun e(format: String, tr: Throwable, vararg args: Any): Int = e(getTag(""), format(format, *args), tr)

    // What-a-terrible-failure
    fun wtf(tag: String, msg: String): Int = Log.wtf(tag, decorateMessage(msg))
    fun wtf(tag: String, format: String, vararg args: Any): Int = wtf(tag, format(format, *args))
    fun wtf(tag: String, msg: String, tr: Throwable): Int = Log.wtf(tag, decorateMessage(msg), tr)
    fun wtf(tag: String, format: String, tr: Throwable, vararg args: Any): Int = wtf(tag, format(format, *args), tr)

    fun wtf(msg: String): Int = Log.wtf(getTag(""), decorateMessage(msg))
    fun wtf(format: String, vararg args: Any): Int = wtf(getTag(""), format(format, *args))
    fun wtf(msg: String, tr: Throwable): Int = Log.wtf(getTag(""), decorateMessage(msg), tr)
    fun wtf(format: String, tr: Throwable, vararg args: Any): Int = wtf(getTag(""), format(format, *args), tr)

    private fun getTag(tag: String): String {
        return when {
            tag.isNotBlank() -> tag
            LogDogConfig.autoTag -> getAutoTag()
            else ->  getAutoTag()
        }
    }

    private fun getAutoTag(): String {
        // Walk up the stack trace until the callee is found, then get its classname
        val index = Thread.currentThread().stackTrace.indexOfLast { it.className.contains("me.levansmith.logdog.library.LogDog") } + 1
        return Thread.currentThread().stackTrace[index].className
    }

    private fun format(format: String, vararg args: Any): String = String.format(Locale.getDefault(), format, *args)

    private fun decorateMessage(msg: String): String = if (LogDogConfig.showThreadInfo) logThread(msg) else msg

    private fun logThread(msg: String): String = format("[thread=%s] %s", Thread.currentThread().name, msg)

    private fun logByLevel(tag: String, logLevel: Int, message: String) {
        logByLevel(tag, logLevel) { message }
    }

    private inline fun logByLevel(tag: String, logLevel: Int, crossinline block: () -> String) {
        when (logLevel) {
            Log.VERBOSE -> v(tag, block())
            Log.DEBUG -> d(tag, block())
            Log.INFO -> i(tag, block())
            Log.WARN -> w(tag, block())
            Log.ERROR -> e(tag, block())
            Log.ASSERT -> wtf(tag, block())
            else -> v(tag, block())
        }
    }

    private fun formatMillis(millis: Long, format: String): String {
        if (millis >= 1000) {
            val date = Date(millis)
            val formatter = SimpleDateFormat(format)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(date)
        }
        return "${millis}ms"
    }

    fun count(tag: String, label: String = DEFAULT_COUNT_LABEL, logLevel: Int = Log.VERBOSE) {
        logByLevel(tag, logLevel) {
            if (counters[label] == null) {
                counters[label] = 0L
            }
            counters[label] = counters[label]!!.inc()
            counters[label]!!.toString()
        }
    }

    fun count(label: String = DEFAULT_COUNT_LABEL, logLevel: Int = Log.VERBOSE) {
        count(getTag(""), label, logLevel)
    }

    fun countReset(label: String = DEFAULT_COUNT_LABEL) {
        counters[label] = 0
    }

    fun timeStart(tag: String, label: String = DEFAULT_TIME_LABEL, logLevel: Int = Log.VERBOSE) {
        timers[label] = TimerParams(tag, logLevel, System.currentTimeMillis())
    }

    fun timeEnd(label: String = DEFAULT_TIME_LABEL, format: String = DEFAULT_TIME_FORMAT) {
        val timerParams = timers[label]!!

        logByLevel(timerParams.tag, timerParams.logLevel) {
            formatMillis(System.currentTimeMillis() - timerParams.start, format)
        }
        timerParams.start = 0
    }

    fun timeLog(label: String, format: String = DEFAULT_TIME_FORMAT) {
        val timerParams = timers[label]!!

        logByLevel(timerParams.tag, timerParams.logLevel) {
            formatMillis(System.currentTimeMillis() - timerParams.start, format)
        }
    }

    /**
     * Construct a table with the given collection of data.
     */
    fun <T : Any> table(
        tag: String,
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        logLevel: Int = Log.VERBOSE,
        interpreter: (T) -> List<String>
    ) {
        val indexWidth = data.size.toString().length + 2 //2 spaces for padding

        // COLUMN WIDTHS
        var columnWidths = data.fold(listOf<Int>()) { res, datum ->
            // Lengths of strings for each datum
            val lengths = interpreter(datum).map { it.length }
            if (headers != null && headers.size != lengths.size) throw IndexOutOfBoundsException("Number of table headers does not match the number of data columns")
            if (res.isEmpty()) {
                return@fold lengths
            }
            return@fold res.mapIndexed { i, it ->
                max(max(it, lengths[i]), headers?.get(i)?.length ?: 0)
            }
        }.toMutableList()
        if (showIndexes) columnWidths.add(0, indexWidth - 2)
        columnWidths = columnWidths.map { it + 2 }.toMutableList()

        // Output top row
        val builder = StringBuilder()
        logByLevel(tag, logLevel) {
            builder.outputRow("┏", "┳", "━", "┓", columnWidths).toString()
        }
        builder.clear()

        // Output headers row
        if (headers != null) {
            builder.append("┃")
            if (showIndexes) {
                builder.append(" ".repeat(indexWidth))
                    .append("┃")
            }
            headers.forEachIndexed { i, it ->
                builder.applyPadding(it, columnWidths[i + showIndexes.toInt()])
                    .append("┃")
            }
            logByLevel(tag, logLevel, builder.toString())
            builder.clear()

            // Output headers bottom row
            logByLevel(tag, logLevel) {
                builder.outputRow("┣", "╋", "━", "┫", columnWidths).toString()
            }
            builder.clear()
        }

        // Output data
        data.map { interpreter(it) }.forEachIndexed { index, it ->
            builder.append("┃")
            if (showIndexes) {
                builder.applyPadding("$index", indexWidth, Alignment.RIGHT)
                    .append("┃")
            }

            it.forEachIndexed { i, s ->
                builder.applyPadding(s, columnWidths[i + showIndexes.toInt()])
                    .append("┃")
            }
            logByLevel(tag, logLevel, builder.toString())
            builder.clear()
        }
        // Output bottom row
        logByLevel(tag, logLevel) {
            builder.outputRow("┗", "┻", "━", "┛", columnWidths).toString()
        }
    }

    private fun calcPadding(string: String, columnWidth: Int, alignment: Alignment = Alignment.CENTER) =
        when (alignment) {
            Alignment.CENTER -> {
                val diff = columnWidth - string.length
                val left = diff / 2 //Left bias
                Pair(left, diff - left)
            }
            Alignment.LEFT -> Pair(1, columnWidth - 1 - string.length)
            Alignment.RIGHT -> Pair(columnWidth - 1 - string.length, 1)
        }

    private fun StringBuilder.applyPadding(
        string: String,
        columnWidth: Int,
        alignment: Alignment = Alignment.CENTER
    ): StringBuilder {
        val padding = calcPadding(string, columnWidth, alignment)
        return this
            .append(" ".repeat(padding.first))
            .append(string)
            .append(" ".repeat(padding.second))
    }

    private fun StringBuilder.outputRow(
        startChar: String,
        midChar: String,
        spanChar: String,
        endChar: String,
        columnWidths: List<Int>
    ): StringBuilder {
        this.append(startChar)
        columnWidths.forEachIndexed { i, it ->
            this.append(spanChar.repeat(it))
            if (i != columnWidths.lastIndex) this.append(midChar)
        }
        return this.append(endChar)
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    fun json(tag: String, json: String, logLevel: Int = Log.VERBOSE) {
        try {
            val obj = JSONObject(json).toString(2)
            surroundWithBorder(splitByLine(obj)).forEach { logByLevel(tag, logLevel, it) }

        } catch (e : JSONException) {
            //TODO: Do something here...
        }
    }

    inline fun <reified T, S : JsonSerializer<T>> json(tag: String, obj: T, serializer: S, logLevel: Int = Log.VERBOSE) {
        val gson = GsonBuilder()
            .registerTypeAdapter(T::class.java, serializer)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
        json(tag, gson.toJson(obj), logLevel)
    }

    inline fun <reified T, A : TypeAdapter<T>> json(tag: String, obj: T, adapter: A, logLevel: Int = Log.VERBOSE) {
        val gson = GsonBuilder()
            .registerTypeAdapter(T::class.java, adapter)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
        json(tag, gson.toJson(obj), logLevel)
    }

    fun xml(tag: String, xml: String, logLevel: Int = Log.VERBOSE) {

    }

    private fun splitByLine(content: String): List<String> = content.split(System.getProperty("line.separator")!!)

    private fun surroundWithBorder(lines: List<String>): List<String> {
        if (lines.isEmpty()) return lines
        val longest = lines.maxBy { it.length }!!.length + 2 //2 spaces for padding
        val result = mutableListOf<String>()
        result.add("┏${"━".repeat(longest)}┓")
        lines.forEach {
            result.add("┃$it${" ".repeat(longest - it.length)}┃")
        }
        result.add("┗${"━".repeat(longest)}┛")
        return result
    }

    object Tag {
        fun create(c: Class<*>): String = c.simpleName
    }

    enum class Alignment { LEFT, RIGHT, CENTER }

    private data class TimerParams(
        val tag: String,
        val logLevel: Int,
        var start: Long
    )
}

