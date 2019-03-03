package me.levansmith.logdog.library

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import org.json.JSONException
import org.json.JSONObject
import java.io.StringWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import kotlin.math.max

object LogDog {

    private const val DEFAULT_COUNT_LABEL = "me.levansmith.logdog.library.LogDog.DEFAULT_COUNT_LABEL"
    private const val DEFAULT_TIME_LABEL = "me.levansmith.logdog.library.LogDog.DEFAULT_TIME_LABEL"

    private const val DEFAULT_TIME_FORMAT = "HH'h' mm'm' ss.SSS's'"

    // For convenience
    const val VERBOSE = Log.VERBOSE
    const val DEBUG = Log.DEBUG
    const val INFO = Log.INFO
    const val WARN = Log.WARN
    const val ERROR = Log.ERROR
    const val ASSERT = Log.ASSERT


    private val counters: MutableMap<String, Long> = mutableMapOf()
    private val timers: MutableMap<String, TimerParams> = mutableMapOf()

    private var willSend: Boolean = false
    private var willForce: Boolean = false
    private var willHide: Boolean = false
    private var level: Int = VERBOSE

    /** Log at a VERBOSE level */
    val v: LogDog get() { level = VERBOSE; return this }
    /** Log at a DEBUG level */
    val d: LogDog get() { level = DEBUG; return this }
    /** Log at an INFO level */
    val i: LogDog get() { level = INFO; return this }
    /** Log at a WARN level */
    val w: LogDog get() { level = WARN; return this }
    /** Log at an ERROR level */
    val e: LogDog get() { level = ERROR; return this }
    /** Log at an ASSERT level */
    val wtf: LogDog get() { level = ASSERT; return this }

    /** Send the message to the configured analytics service */
    val send: LogDog get() { willSend = true; return this }

    /** Output the log despite all configurations */
    val force: LogDog get() { willForce = true; return this }

    /** Prevent log output despite all configurations, except <pre>force</pre> */
    val hide: LogDog get() { willHide = true; return this }

    // Whenever a particular option is set, it needs to be reset when finished
    private fun resetOptions() {
        willSend = false
        willForce = false
        willHide = false
        level = VERBOSE
    }

    // Verbose
    fun v(tag: String, msg: String) = dispatch(VERBOSE, tag, msg)
    fun v(tag: String, format: String, vararg args: Any): Int = dispatch(VERBOSE, tag, format = format, args = *args)
    fun v(tag: String, msg: String, tr: Throwable): Int = dispatch(VERBOSE, tag, msg, tr = tr)
    fun v(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(VERBOSE, tag, format = format, tr = tr, args = *args)

    fun v(msg: String): Int = dispatch(VERBOSE, msg = msg)
    fun v(format: String, vararg args: Any): Int = dispatch(VERBOSE, format = format, args = *args)
    fun v(msg: String, tr: Throwable): Int = dispatch(VERBOSE, msg = msg, tr = tr)
    fun v(format: String, tr: Throwable, vararg args: Any): Int = dispatch(VERBOSE, format = format, tr = tr, args = *args)

    // Debug
    fun d(tag: String, msg: String) = dispatch(DEBUG, tag, msg)
    fun d(tag: String, format: String, vararg args: Any): Int = dispatch(DEBUG, tag, format = format, args = *args)
    fun d(tag: String, msg: String, tr: Throwable): Int = dispatch(DEBUG, tag, msg, tr = tr)
    fun d(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(DEBUG, tag, format = format, tr = tr, args = *args)

    fun d(msg: String): Int = dispatch(DEBUG, msg = msg)
    fun d(format: String, vararg args: Any): Int = dispatch(DEBUG, format = format, args = *args)
    fun d(msg: String, tr: Throwable): Int = dispatch(DEBUG, msg = msg, tr = tr)
    fun d(format: String, tr: Throwable, vararg args: Any): Int = dispatch(DEBUG, format = format, tr = tr, args = *args)

    // Info
    fun i(tag: String, msg: String) = dispatch(INFO, tag, msg)
    fun i(tag: String, format: String, vararg args: Any): Int = dispatch(INFO, tag, format = format, args = *args)
    fun i(tag: String, msg: String, tr: Throwable): Int = dispatch(INFO, tag, msg, tr = tr)
    fun i(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(INFO, tag, format = format, tr = tr, args = *args)

    fun i(msg: String): Int = dispatch(INFO, msg = msg)
    fun i(format: String, vararg args: Any): Int = dispatch(INFO, format = format, args = *args)
    fun i(msg: String, tr: Throwable): Int = dispatch(INFO, msg = msg, tr = tr)
    fun i(format: String, tr: Throwable, vararg args: Any): Int = dispatch(INFO, format = format, tr = tr, args = *args)

    // Warn
    fun w(tag: String, msg: String) = dispatch(WARN, tag, msg)
    fun w(tag: String, format: String, vararg args: Any): Int = dispatch(WARN, tag, format = format, args = *args)
    fun w(tag: String, msg: String, tr: Throwable): Int = dispatch(WARN, tag, msg, tr = tr)
    fun w(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(WARN, tag, format = format, tr = tr, args = *args)

    fun w(msg: String): Int = dispatch(WARN, msg = msg)
    fun w(format: String, vararg args: Any): Int = dispatch(WARN, format = format, args = *args)
    fun w(msg: String, tr: Throwable): Int = dispatch(WARN, msg = msg, tr = tr)
    fun w(format: String, tr: Throwable, vararg args: Any): Int = dispatch(WARN, format = format, tr = tr, args = *args)

    // Error
    fun e(tag: String, msg: String) = dispatch(ERROR, tag, msg)
    fun e(tag: String, format: String, vararg args: Any): Int = dispatch(ERROR, tag, format = format, args = *args)
    fun e(tag: String, msg: String, tr: Throwable): Int = dispatch(ERROR, tag, msg, tr = tr)
    fun e(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(ERROR, tag, format = format, tr = tr, args = *args)

    fun e(msg: String): Int = dispatch(ERROR, msg = msg)
    fun e(format: String, vararg args: Any): Int = dispatch(ERROR, format = format, args = *args)
    fun e(msg: String, tr: Throwable): Int = dispatch(ERROR, msg = msg, tr = tr)
    fun e(format: String, tr: Throwable, vararg args: Any): Int = dispatch(ERROR, format = format, tr = tr, args = *args)

    // What-a-terrible-failure
    fun wtf(tag: String, msg: String) = dispatch(ASSERT, tag, msg)
    fun wtf(tag: String, format: String, vararg args: Any): Int = dispatch(ASSERT, tag, format = format, args = *args)
    fun wtf(tag: String, msg: String, tr: Throwable): Int = dispatch(ASSERT, tag, msg, tr = tr)
    fun wtf(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(ASSERT, tag, format = format, tr = tr, args = *args)

    fun wtf(msg: String): Int = dispatch(ASSERT, msg = msg)
    fun wtf(format: String, vararg args: Any): Int = dispatch(ASSERT, format = format, args = *args)
    fun wtf(msg: String, tr: Throwable): Int = dispatch(ASSERT, msg = msg, tr = tr)
    fun wtf(format: String, tr: Throwable, vararg args: Any): Int = dispatch(ASSERT, format = format, tr = tr, args = *args)

    fun log(tag: String, msg: String) = dispatch(level, tag, msg)
    fun log(tag: String, format: String, vararg args: Any): Int = dispatch(level, tag, format = format, args = *args)
    fun log(tag: String, msg: String, tr: Throwable): Int = dispatch(level, tag, msg, tr = tr)
    fun log(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(level, tag, format = format, tr = tr, args = *args)
    fun log(msg: String): Int = dispatch(level, msg = msg)
    fun log(format: String, vararg args: Any): Int = dispatch(level, format = format, args = *args)
    fun log(msg: String, tr: Throwable): Int = dispatch(level, msg = msg, tr = tr)
    fun log(format: String, tr: Throwable, vararg args: Any): Int = dispatch(level, format = format, tr = tr, args = *args)

    private fun preDispatch(logLevel: Int): Boolean {
        if (willForce) return true

        // Determine whether to hide
        return !(LogDogConfig.disableLogs || willHide || logLevel < LogDogConfig.logThreshold)
    }
    private fun dispatch(logLevel: Int, tag: String = getTag(""), msg: String = "", format: String? = null, tr: Throwable? = null, vararg args: Any): Int {
        if (!preDispatch(logLevel)) return 0
        //TODO: Dispatch event to analytics
        val formatted: String = if (format != null) LogDog.format(format, *args) else msg
        val result: Int = when(logLevel) {
            VERBOSE -> if (tr != null) Log.v(tag, decorateMessage(formatted), tr) else Log.v(tag, decorateMessage(formatted))
            DEBUG -> if (tr != null) Log.d(tag, decorateMessage(formatted), tr) else Log.d(tag, decorateMessage(formatted))
            INFO -> if (tr != null) Log.i(tag, decorateMessage(formatted), tr) else Log.i(tag, decorateMessage(formatted))
            WARN -> if (tr != null) Log.w(tag, decorateMessage(formatted), tr) else Log.w(tag, decorateMessage(formatted))
            ERROR -> if (tr != null) Log.e(tag, decorateMessage(formatted), tr) else Log.e(tag, decorateMessage(formatted))
            ASSERT -> if (tr != null) Log.wtf(tag, decorateMessage(formatted), tr) else Log.wtf(tag, decorateMessage(formatted))
            else -> throw Exception("Unknown log level")
        }
        postDispatch()
        return result
    }
    private fun postDispatch() {
        resetOptions()
    }

    private fun getTag(tag: String): String {
        return when {
            tag.isNotBlank() -> tag
            LogDogConfig.autoTag -> getAutoTag()
            LogDogConfig.tag.isNotBlank() -> LogDogConfig.tag
            else ->  getAutoTag()
        }
    }

    private fun getAutoTag(): String {
        // Walk up the stack trace until the callee is found, then get its classname
        val className = this::class.java.name
        val index = Thread.currentThread().stackTrace.indexOfLast { it.className.contains(className) } + 1
        return Thread.currentThread().stackTrace[index].className
    }

    private fun format(format: String, vararg args: Any): String = String.format(Locale.getDefault(), format, *args)

    private fun decorateMessage(msg: String): String = if (LogDogConfig.showThreadInfo) logThread(msg) else msg

    private fun logThread(msg: String): String = format("[thread=%s] %s", Thread.currentThread().name, msg)

    private fun logByLevel(tag: String, logLevel: Int, message: String) { logByLevel(tag, logLevel) { message } }

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
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(date)
        }
        return "${millis}ms"
    }

    fun count(tag: String, label: String = DEFAULT_COUNT_LABEL) {
        logByLevel(tag, level) {
            if (counters[label] == null) {
                counters[label] = 0L
            }
            counters[label] = counters[label]!!.inc()
            "$label: ${counters[label]!!}"
        }
    }

    fun count(label: String = DEFAULT_COUNT_LABEL) {
        count(getTag(""), label)
    }

    fun countReset(tag: String, label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        counters[label] = 0
        if (shouldStateReset) {
            LogDog.logByLevel(tag, level, "$label was reset")
        } else {
            //If this was erroneously called with a log level or other option, it should be reset
            resetOptions()
        }
    }

    fun countReset(label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        countReset(getTag(""), label, shouldStateReset)
    }

    fun timeStart(tag: String, label: String = DEFAULT_TIME_LABEL) {
        timers[label] = TimerParams(tag, level, System.currentTimeMillis())
        //If this was called with a log level or other option, it should be reset because it's not used immediately
        resetOptions()
    }

    fun timeStart(label: String = DEFAULT_TIME_LABEL) {
        timeStart(getTag(""), label)
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
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        LogDog.table(getTag(""), data, headers, showIndexes, interpreter)
    }
    fun <T : Any> table(
        tag: String,
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        val logLevel = level
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

    fun json(tag: String?, json: String) {

        try {
            val obj = JSONObject(json).toString(2)
            val logLevel = level
            surroundWithBorder(splitByLine(obj)).forEach {
                logByLevel(tag ?: getTag(""), logLevel, it)
            }

        } catch (e : JSONException) {
            e("Invalid JSON", e)
        }
    }

    fun json(json: String) {
        LogDog.json(getTag(""), json)
    }

    inline fun <reified T, S : JsonSerializer<T>> json(tag: String?, obj: T, serializer: S) {
        val gson = GsonBuilder()
            .registerTypeAdapter(T::class.java, serializer)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
        json(tag, gson.toJson(obj))
    }

    inline fun <reified T, S : JsonSerializer<T>> json(obj: T, serializer: S) {
        json(null, obj, serializer)
    }

    inline fun <reified T, A : TypeAdapter<T>> json(tag: String?, obj: T, adapter: A) {
        val gson = GsonBuilder()
            .registerTypeAdapter(T::class.java, adapter)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
        json(tag, gson.toJson(obj))
    }
    inline fun <reified T, A : TypeAdapter<T>> json(obj: T, adapter: A) {
        json(null, obj, adapter)
    }

    fun xml(tag: String, xml: String) {
        val logLevel = level
        try {
            val source = StreamSource(xml.reader())
            val result = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(source, result)
            val lines = surroundWithBorder(splitByLine(result.writer.toString().replaceFirst(">", ">\n"))).toMutableList()
            lines.removeAt(lines.lastIndex - 1) // It always had an extra line at the end
            lines.forEach { LogDog.logByLevel(tag, logLevel, it) }
        } catch (e: TransformerException) {
            e("Invalid XML", e)
        }
    }

    fun xml(xml: String) {
        xml(getTag(""), xml)
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

