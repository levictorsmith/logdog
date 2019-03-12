package me.levansmith.logdog.library

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import org.json.JSONException
import org.json.JSONObject
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import kotlin.math.max

abstract class Dispatcher {

    internal companion object {
        // For convenience
        internal const val VERBOSE = Log.VERBOSE
        internal const val DEBUG = Log.DEBUG
        internal const val INFO = Log.INFO
        internal const val WARN = Log.WARN
        internal const val ERROR = Log.ERROR
        internal const val ASSERT = Log.ASSERT

        private const val DEFAULT_LOG_EVENT_NAME = "log_event"
        private const val DEFAULT_COUNT_LABEL = "me.levansmith.logdog.library.Dispatcher.DEFAULT_COUNT_LABEL"
        private const val DEFAULT_TIME_LABEL = "me.levansmith.logdog.library.Dispatcher.DEFAULT_TIME_LABEL"

        private const val DEFAULT_TIME_FORMAT = "HH'h' mm'm' ss.SSS's'"
    }

    protected abstract fun getOptions(logLevel: Int?): DispatchOptions

    private val counters: MutableMap<String, Long> = mutableMapOf()
    private val timers: MutableMap<String, TimerParams> = mutableMapOf()

    protected data class DispatchOptions(
        var logLevel: Int = Log.VERBOSE,
        var willHide: Boolean = false,
        var willForce: Boolean = false,
        var willSend: Boolean = false
    ) {
        companion object {
            const val HIDE = "me.levansmith.logdog.library.Dispatcher.DispatchOptions.HIDE"
            const val FORCE = "me.levansmith.logdog.library.Dispatcher.DispatchOptions.FORCE"
            const val SEND = "me.levansmith.logdog.library.Dispatcher.DispatchOptions.SEND"
            const val LEVEL = "me.levansmith.logdog.library.Dispatcher.DispatchOptions.LEVEL"
        }
    }

    protected operator fun DispatchOptions.set(key: String, value: Boolean) {
        when(key) {
            DispatchOptions.HIDE -> { willHide = value }
            DispatchOptions.FORCE -> { willForce = value }
            DispatchOptions.SEND -> { willSend = value }
        }
    }

    protected operator fun DispatchOptions.set(key: String, value: Int) {
        when(key) {
            DispatchOptions.LEVEL -> { logLevel = value }
        }
    }

    private data class TimerParams(
        val tag: String,
        val options: DispatchOptions,
        var start: Long
    )

    private data class LogEvent(
        val tag: String,
        val msg: String,
        val exception: Throwable?
    ) : AnalyticsEvent(DEFAULT_LOG_EVENT_NAME) {

        override fun getFields() = mutableMapOf<String, String>().apply {
            put("log_tag", tag)
            put("log_message", msg)
            if (exception != null) put("log_exception", exception.message ?: "")
        }
    }

    // Log (AKA Verbose)
    public fun log(tag: String, msg: String) = dispatch(getOptions(null), tag, msg)
    public fun log(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(null), tag, format = format, args = *args)
    public fun log(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(null), tag, msg, tr = tr)
    public fun log(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(null), tag, format = format, tr = tr, args = *args)

    public fun log(msg: String): Int = dispatch(getOptions(null), msg = msg)
    public fun log(format: String, vararg args: Any): Int = dispatch(getOptions(null), format = format, args = *args)
    public fun log(msg: String, tr: Throwable): Int = dispatch(getOptions(null), msg = msg, tr = tr)
    public fun log(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(null), format = format, tr = tr, args = *args)

    public fun log(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(null), tag, event = event)
    public fun log(event: AnalyticsEvent): Int = dispatch(getOptions(null), event = event)

    // Verbose
    public fun v(tag: String, msg: String) = dispatch(getOptions(VERBOSE), tag, msg)
    public fun v(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(VERBOSE), tag, format = format, args = *args)
    public fun v(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(VERBOSE), tag, msg, tr = tr)
    public fun v(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(VERBOSE), tag, format = format, tr = tr, args = *args)

    public fun v(msg: String): Int = dispatch(getOptions(VERBOSE), msg = msg)
    public fun v(format: String, vararg args: Any): Int = dispatch(getOptions(VERBOSE), format = format, args = *args)
    public fun v(msg: String, tr: Throwable): Int = dispatch(getOptions(VERBOSE), msg = msg, tr = tr)
    public fun v(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(VERBOSE), format = format, tr = tr, args = *args)

    public fun v(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(VERBOSE), tag, event = event)
    public fun v(event: AnalyticsEvent): Int = dispatch(getOptions(VERBOSE), event = event)

    // Debug
    public fun d(tag: String, msg: String) = dispatch(getOptions(DEBUG), tag, msg)
    public fun d(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(DEBUG), tag, format = format, args = *args)
    public fun d(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(DEBUG), tag, msg, tr = tr)
    public fun d(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(DEBUG), tag, format = format, tr = tr, args = *args)

    public fun d(msg: String): Int = dispatch(getOptions(DEBUG), msg = msg)
    public fun d(format: String, vararg args: Any): Int = dispatch(getOptions(DEBUG), format = format, args = *args)
    public fun d(msg: String, tr: Throwable): Int = dispatch(getOptions(DEBUG), msg = msg, tr = tr)
    public fun d(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(DEBUG), format = format, tr = tr, args = *args)

    public fun d(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(DEBUG), tag, event = event)
    public fun d(event: AnalyticsEvent): Int = dispatch(getOptions(DEBUG), event = event)

    // Info
    public fun i(tag: String, msg: String) = dispatch(getOptions(INFO), tag, msg)
    public fun i(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(INFO), tag, format = format, args = *args)
    public fun i(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(INFO), tag, msg, tr = tr)
    public fun i(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(INFO), tag, format = format, tr = tr, args = *args)

    public fun i(msg: String): Int = dispatch(getOptions(INFO), msg = msg)
    public fun i(format: String, vararg args: Any): Int = dispatch(getOptions(INFO), format = format, args = *args)
    public fun i(msg: String, tr: Throwable): Int = dispatch(getOptions(INFO), msg = msg, tr = tr)
    public fun i(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(INFO), format = format, tr = tr, args = *args)

    public fun i(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(INFO), tag, event = event)
    public fun i(event: AnalyticsEvent): Int = dispatch(getOptions(INFO), event = event)

    // Warn
    public fun w(tag: String, msg: String) = dispatch(getOptions(WARN), tag, msg)
    public fun w(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(WARN), tag, format = format, args = *args)
    public fun w(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(WARN), tag, msg, tr = tr)
    public fun w(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(WARN), tag, format = format, tr = tr, args = *args)

    public fun w(msg: String): Int = dispatch(getOptions(WARN), msg = msg)
    public fun w(format: String, vararg args: Any): Int = dispatch(getOptions(WARN), format = format, args = *args)
    public fun w(msg: String, tr: Throwable): Int = dispatch(getOptions(WARN), msg = msg, tr = tr)
    public fun w(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(WARN), format = format, tr = tr, args = *args)

    public fun w(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(WARN), tag, event = event)
    public fun w(event: AnalyticsEvent): Int = dispatch(getOptions(WARN), event = event)

    // Error
    public fun e(tag: String, msg: String) = dispatch(getOptions(ERROR), tag, msg)
    public fun e(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(ERROR), tag, format = format, args = *args)
    public fun e(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(ERROR), tag, msg, tr = tr)
    public fun e(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(ERROR), tag, format = format, tr = tr, args = *args)

    public fun e(msg: String): Int = dispatch(getOptions(ERROR), msg = msg)
    public fun e(format: String, vararg args: Any): Int = dispatch(getOptions(ERROR), format = format, args = *args)
    public fun e(msg: String, tr: Throwable): Int = dispatch(getOptions(ERROR), msg = msg, tr = tr)
    public fun e(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(ERROR), format = format, tr = tr, args = *args)

    public fun e(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(ERROR), tag, event = event)
    public fun e(event: AnalyticsEvent): Int = dispatch(getOptions(ERROR), event = event)

    // What-a-terrible-failure
    public fun wtf(tag: String, msg: String) = dispatch(getOptions(ASSERT), tag, msg)
    public fun wtf(tag: String, format: String, vararg args: Any): Int = dispatch(getOptions(ASSERT), tag, format = format, args = *args)
    public fun wtf(tag: String, msg: String, tr: Throwable): Int = dispatch(getOptions(ASSERT), tag, msg, tr = tr)
    public fun wtf(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(ASSERT), tag, format = format, tr = tr, args = *args)

    public fun wtf(msg: String): Int = dispatch(getOptions(ASSERT), msg = msg)
    public fun wtf(format: String, vararg args: Any): Int = dispatch(getOptions(ASSERT), format = format, args = *args)
    public fun wtf(msg: String, tr: Throwable): Int = dispatch(getOptions(ASSERT), msg = msg, tr = tr)
    public fun wtf(format: String, tr: Throwable, vararg args: Any): Int = dispatch(getOptions(ASSERT), format = format, tr = tr, args = *args)

    public fun wtf(tag: String, event: AnalyticsEvent): Int = dispatch(getOptions(ASSERT), tag, event = event)
    public fun wtf(event: AnalyticsEvent): Int = dispatch(getOptions(ASSERT), event = event)

    public fun count(tag: String, label: String = DEFAULT_COUNT_LABEL) {
        count(getOptions(null), tag, label)
    }

    public fun count(label: String = DEFAULT_COUNT_LABEL) {
        count(getTag(""), label)
    }

    public fun countReset(tag: String, label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        countReset(getOptions(null), tag, label, shouldStateReset)
    }

    public fun countReset(label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        countReset(getTag(""), label, shouldStateReset)
    }

    public fun getCount(label: String): Long {
        return counters[label] ?: 0
    }

    public fun timeStart(tag: String, label: String = DEFAULT_TIME_LABEL) {
        timeStart(getOptions(null), tag, label)
    }

    public fun timeStart(label: String = DEFAULT_TIME_LABEL) {
        timeStart(getTag(""), label)
    }

    public fun timeEnd(label: String = DEFAULT_TIME_LABEL, format: String = DEFAULT_TIME_FORMAT) {
        val timerParams = timers[label]!!

        dispatch(timerParams.options, timerParams.tag) {
            formatMillis(System.currentTimeMillis() - timerParams.start, format)
        }
        timerParams.start = 0
    }

    public fun timeLog(label: String, format: String = DEFAULT_TIME_FORMAT) {
        val timerParams = timers[label]!!


        dispatch(timerParams.options, timerParams.tag) {
            formatMillis(System.currentTimeMillis() - timerParams.start, format)
        }
    }

    /**
     * Construct a table with the given collection of data.
     */
    public fun <T : Any> table(
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        table(getTag(""), data, headers, showIndexes, interpreter)
    }

    public fun <T : Any> table(
        tag: String,
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        table(getOptions(null), tag, data, headers, showIndexes, interpreter)
    }

    public fun json(json: String) {
        json(getTag(""), json)
    }

    public fun json(tag: String?, json: String) {
        json(getOptions(null), tag, json)
    }

    public inline fun <reified T, S : JsonSerializer<T>> json(tag: String?, obj: T, serializer: S) {
        val gson = GsonBuilder()
            .registerTypeAdapter(T::class.java, serializer)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
        json(tag, gson.toJson(obj))
    }

    public inline fun <reified T, S : JsonSerializer<T>> json(obj: T, serializer: S) {
        json(null, obj, serializer)
    }

    public inline fun <reified T, A : TypeAdapter<T>> json(tag: String?, obj: T, adapter: A) {
        val gson = GsonBuilder()
            .registerTypeAdapter(T::class.java, adapter)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
        json(tag, gson.toJson(obj))
    }

    public inline fun <reified T, A : TypeAdapter<T>> json(obj: T, adapter: A) {
        json(null, obj, adapter)
    }

    public fun xml(tag: String, xml: String) {
        xml(getOptions(null), tag, xml)
    }

    public fun xml(xml: String) {
        xml(getTag(""), xml)
    }

    /*
     DISPATCH PROCESS
     */
    private fun preDispatch(force: Boolean, hide: Boolean, logLevel: Int): Boolean {
        if (force) return true

        // Determine whether to hide
        return !(LogDogConfig.disableLogs || hide || logLevel < LogDogConfig.logThreshold)
    }

    private inline fun dispatch(options: DispatchOptions, tag: String = getTag(""), crossinline block: () -> String) {
        dispatch(options, tag, block())
    }

    private fun dispatch(options: DispatchOptions, tag: String = getTag(""), msg: String = "", format: String? = null, tr: Throwable? = null, event: AnalyticsEvent? = null, vararg args: Any): Int {
        // Pre-dispatch
        if (!preDispatch(options.willForce, options.willHide, options.logLevel)) return 0

        // Dispatch
        var message: String = if (format != null) format(format, *args) else msg
        if (event != null) {
            message =  event.toString()
        }
        if (options.willSend) sendEvent(tag, msg, tr, event)
        val result: Int = when(options.logLevel) {
            VERBOSE -> if (tr != null) Log.v(tag, decorateMessage(message), tr) else Log.v(tag, decorateMessage(message))
            DEBUG -> if (tr != null) Log.d(tag, decorateMessage(message), tr) else Log.d(tag, decorateMessage(message))
            INFO -> if (tr != null) Log.i(tag, decorateMessage(message), tr) else Log.i(tag, decorateMessage(message))
            WARN -> if (tr != null) Log.w(tag, decorateMessage(message), tr) else Log.w(tag, decorateMessage(message))
            ERROR -> if (tr != null) Log.e(tag, decorateMessage(message), tr) else Log.e(tag, decorateMessage(message))
            ASSERT -> if (tr != null) Log.wtf(tag, decorateMessage(message), tr) else Log.wtf(tag, decorateMessage(message))
            else -> throw Exception("Unknown log level")
        }

        // Post-dispatch
        postDispatch()
        return result
    }
    private fun postDispatch() {
    }

    private fun sendEvent(tag: String, msg: String, tr: Throwable?, event: AnalyticsEvent?) {
        LogDogConfig.analyticsHandler.invoke(event ?: LogEvent(tag, msg, tr))
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
        val className = Dispatcher::class.java.name
        val index = Thread.currentThread().stackTrace.indexOfLast { it.className.contains(className) } + 1
        return Thread.currentThread().stackTrace[index].className
    }

    private fun format(format: String, vararg args: Any): String = String.format(Locale.getDefault(), format, *args)

    private fun decorateMessage(msg: String): String = if (LogDogConfig.showThreadInfo) logThread(msg) else msg

    private fun logThread(msg: String): String = format("[thread=%s] %s", Thread.currentThread().name, msg)

    private fun formatMillis(millis: Long, format: String): String {
        if (millis >= 1000) {
            val date = Date(millis)
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(date)
        }
        return "${millis}ms"
    }

    /*
     COUNT
     */
    private fun count(options: DispatchOptions, tag: String, label: String = DEFAULT_COUNT_LABEL) {
        dispatch(options, tag) {
            if (counters[label] == null) {
                counters[label] = 0L
            }
            counters[label] = counters[label]!!.inc()
            "$label: ${counters[label]!!}"
        }
    }

    /*
     COUNT RESET
     */
    private fun countReset(options: DispatchOptions, tag: String, label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        counters[label] = 0
        if (shouldStateReset) {
            dispatch(options, tag, "$label was reset")
        }
    }

    /*
     TIME START
     */
    private fun timeStart(options: DispatchOptions, tag: String, label: String = DEFAULT_TIME_LABEL) {
        timers[label] = TimerParams(tag, options, System.currentTimeMillis())
    }

    /*
     TABLE
     */
    private fun <T : Any> table(
        options: DispatchOptions,
        tag: String,
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
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
        dispatch(options, tag) {
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
            dispatch(options, tag, builder.toString())
            builder.clear()

            // Output headers bottom row
            dispatch(options, tag) {
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
            dispatch(options, tag, builder.toString())
            builder.clear()
        }
        // Output bottom row
        dispatch(options, tag) {
            builder.outputRow("┗", "┻", "━", "┛", columnWidths).toString()
        }
    }

    /*
     JSON
     */
    private fun json(options: DispatchOptions, tag: String?, json: String) {

        try {
            val obj = JSONObject(json).toString(2)
            surroundWithBorder(splitByLine(obj)).forEach {
                dispatch(options, tag ?: getTag(""), it)
            }

        } catch (e : JSONException) {
            e("Invalid JSON", e)
        }
    }

    /*
     XML
     */
    private fun xml(options: DispatchOptions, tag: String, xml: String) {
        try {
            val source = StreamSource(xml.reader())
            val result = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(source, result)
            val lines = surroundWithBorder(splitByLine(result.writer.toString().replaceFirst(">", ">\n"))).toMutableList()
            lines.removeAt(lines.lastIndex - 1) // It always had an extra line at the end
            lines.forEach { dispatch(options, tag, it) }
        } catch (e: TransformerException) {
            e("Invalid XML", e)
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

    // TODO: Determine standards around tables/spreadsheets for RTL languages to attempt to provide an encompassing solution
    private fun isRTL(): Boolean {
        val locale = Locale.getDefault()
        val directionality = Character.getDirectionality(locale.displayName[0]).toInt()
        return directionality == CharDirectionality.RIGHT_TO_LEFT.value || directionality == CharDirectionality.RIGHT_TO_LEFT_ARABIC.value
    }

}