package me.levansmith.logging

import com.google.gson.*
import kotlinx.coroutines.*
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
import me.levansmith.logging.Dispatcher.*

abstract class DispatchLogger<M : Modifiers>(private val logProvider: LogProvider) : Dispatcher<M> {

    internal companion object {

        private const val DEFAULT_LOG_EVENT_NAME = "log_event"
        private const val DEFAULT_COUNT_LABEL = "me.levansmith.logging.DispatchLogger.DEFAULT_COUNT_LABEL"
        private const val DEFAULT_TIME_LABEL = "me.levansmith.logging.DispatchLogger.DEFAULT_TIME_LABEL"

        private const val DEFAULT_TIME_FORMAT = "HH'h' mm'm' ss.SSS's'"

        private val counters: MutableMap<String, Long> = mutableMapOf()
        private val timers: MutableMap<String, TimerParams> = mutableMapOf()

    }

    abstract val className: String

    private data class TimerParams(
        val tag: String,
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
    public fun log(tag: String, msg: String) = dispatch(null, Delegate(tag, msg))
    public fun log(tag: String, format: String, vararg args: Any): Int = dispatch(null, Delegate(tag, format = format, args = args.toList()))
    public fun log(tag: String, msg: String, tr: Throwable): Int = dispatch(null, Delegate(tag, msg, error = tr))
    public fun log(tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(null, Delegate(tag, format = format, error = tr, args = args.toList()))

    public fun log(msg: String): Int = dispatch(null, Delegate("", message = msg))
    public fun log(format: String, vararg args: Any): Int = dispatch(null, Delegate("", format = format, args = args.toList()))
    public fun log(msg: String, tr: Throwable): Int = dispatch(null, Delegate("", message = msg, error = tr))
    public fun log(format: String, tr: Throwable, vararg args: Any): Int = dispatch(null, Delegate("", format = format, error = tr, args = args.toList()))

    public fun log(tag: String, event: AnalyticsEvent): Int = dispatch(null, Delegate(tag, event = event))
    public fun log(event: AnalyticsEvent): Int = dispatch(null, Delegate("", event = event))


    // Log by level
    protected fun logByLevel(level: LogProvider.Level, tag: String, msg: String) = dispatch(level, Delegate(tag, msg))
    protected fun logByLevel(level: LogProvider.Level, tag: String, format: String, vararg args: Any): Int = dispatch(level, Delegate(tag, format = format, args = args.toList()))
    protected fun logByLevel(level: LogProvider.Level, tag: String, msg: String, tr: Throwable): Int = dispatch(level, Delegate(tag, msg, error = tr))
    protected fun logByLevel(level: LogProvider.Level, tag: String, format: String, tr: Throwable, vararg args: Any): Int = dispatch(level, Delegate(tag, format = format, error = tr, args = args.toList()))
    protected fun logByLevel(level: LogProvider.Level, msg: String): Int = dispatch(level, Delegate("", message = msg))
    protected fun logByLevel(level: LogProvider.Level, format: String, vararg args: Any): Int = dispatch(level, Delegate("", format = format, args = args.toList()))
    protected fun logByLevel(level: LogProvider.Level, msg: String, tr: Throwable): Int = dispatch(level, Delegate("", message = msg, error = tr))
    protected fun logByLevel(level: LogProvider.Level, format: String, tr: Throwable, vararg args: Any): Int = dispatch(level, Delegate("", format = format, error = tr, args = args.toList()))
    protected fun logByLevel(level: LogProvider.Level, tag: String, event: AnalyticsEvent): Int = dispatch(level, Delegate(tag, event = event))
    protected fun logByLevel(level: LogProvider.Level, event: AnalyticsEvent): Int = dispatch(level, Delegate("", event = event))

    public fun count(tag: String, label: String = DEFAULT_COUNT_LABEL) {
        doCount(tag, label)
    }

    public fun count(label: String = DEFAULT_COUNT_LABEL) {
        count("", label)
    }

    public fun countReset(tag: String, label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        doCountReset(tag, label, shouldStateReset)
    }

    public fun countReset(label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        countReset("", label, shouldStateReset)
    }

    public fun getCount(label: String): Long {
        return counters[label] ?: 0
    }

    public fun timeStart(tag: String, label: String = DEFAULT_TIME_LABEL) {
        doTimeStart(tag, label)
    }

    public fun timeStart(label: String = DEFAULT_TIME_LABEL) {
        timeStart("", label)
    }

    public fun timeEnd(label: String = DEFAULT_TIME_LABEL, format: String = DEFAULT_TIME_FORMAT) {
        val timerParams = timers[label]!!

        dispatch(null, Delegate(timerParams.tag, formatMillis(System.currentTimeMillis() - timerParams.start, format)))
        timerParams.start = 0
    }

    public fun timeLog(label: String, format: String = DEFAULT_TIME_FORMAT) {
        val timerParams = timers[label]!!

        dispatch(null, Delegate(timerParams.tag, formatMillis(System.currentTimeMillis() - timerParams.start, format)))
    }

    /**
     * Construct a table with the given collection of data.
     */
    public fun <T : Any> table(
        tag: String,
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        doTable(tag, data, headers, showIndexes, interpreter)
    }

    public fun <T : Any> table(
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        table("", data, headers, showIndexes, interpreter)
    }

    public fun json(tag: String?, json: String) {
        doJson(tag, json)
    }

    public fun json(json: String) {
        json("", json)
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
        doXml(tag, xml)
    }

    public fun xml(xml: String) {
        xml("", xml)
    }

    override fun shouldDispatch(modifiers: M, delegate: Delegate): Boolean {
        if (modifiers.willForce) return true
        // Determine whether to hide
        // TODO: Request a default value for loglevel?
        return !(LogDogConfig.disableLogs || modifiers.willHide || modifiers.logLevel!! < (LogDogConfig.logThreshold ?: logProvider.min))
    }

    override fun preDispatch(modifiers: M, delegate: Delegate) {
        if (modifiers.willSend) {
            sendEvent(delegate.tag, delegate.message, delegate.error, delegate.event)
        }
    }

    override fun doDispatch(modifiers: M, delegate: Delegate): Int {
        return logProvider.logByLevel(modifiers.logLevel, getTag(delegate.tag), decorateMessage(modifiers, delegate), delegate.error)
    }

    override fun postDispatch(modifiers: M, delegate: Delegate) {
        // Do nothing for now
    }

    private fun sendEvent(tag: String, msg: String, tr: Throwable?, event: AnalyticsEvent?) {
        LogDogConfig.analyticsHandler.invoke(event ?: LogEvent(tag, msg, tr))
    }

    private fun getTag(tag: String = ""): String {
        return when {
            tag.isNotBlank() -> tag
            LogDogConfig.tagGenerator != null -> LogDogConfig.tagGenerator!!.invoke()
            LogDogConfig.autoTag -> getAutoTag()
            LogDogConfig.tag.isNotBlank() -> LogDogConfig.tag
            else -> getAutoTag()
        }
    }

    private fun getAutoTag(): String {
        // Walk up the stack trace until the callee is found, then get its classname
        val className = DispatchLogger::class.java.name
        // Only take the first 15 entries to save on time. We know the last call to anything logdog won't be more than 15 calls out
        val trace = Thread.currentThread().stackTrace.take(15)
        val index = trace.indexOfLast { it.className.contains(className) || it.className.contains(this.className) } + 1
        return Thread.currentThread().stackTrace[index].className
    }

    private fun format(format: String, vararg args: Any): String = String.format(Locale.getDefault(), format, *args)

    private fun decorateMessage(modifiers: Modifiers, delegate: Delegate) = when {
        modifiers.showThreadInfo -> logThread(delegate.message)
        delegate.format != null -> format(delegate.format!!, delegate.args.toTypedArray())
        else -> delegate.message
    }

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
    private fun doCount(tag: String, label: String = DEFAULT_COUNT_LABEL) {
        dispatch(null, Delegate(tag, buildString {
            if (counters[label] == null) {
                counters[label] = 0L
            }
            counters[label] = counters[label]!!.inc()
            append("$label: ${counters[label]!!}")
        }))
    }

    /*
     COUNT RESET
     */
    private fun doCountReset(tag: String, label: String = DEFAULT_COUNT_LABEL, shouldStateReset: Boolean = true) {
        counters[label] = 0
        if (shouldStateReset) {
            dispatch(null, Delegate(tag, "$label was reset"))
        }
    }

    /*
     TIME START
     */
    private fun doTimeStart(tag: String, label: String = DEFAULT_TIME_LABEL) {
        timers[label] = TimerParams(tag, System.currentTimeMillis())
    }

    /*
     TABLE
     */
    private fun <T : Any> doTable(
        tag: String,
        data: Collection<T>,
        headers: List<String>? = null,
        showIndexes: Boolean = true,
        interpreter: (T) -> List<String>
    ) {
        runBlocking(Dispatchers.Default) {
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
            dispatch(null, Delegate(tag, builder.outputRow("┏", "┳", "━", "┓", columnWidths).toString()))
            builder.clear()

            // Output headers row
            if (headers != null) {
                val headersRow = buildRow(headers, columnWidths, showIndexes, indexWidth, null)
//                val headersRow = getHeaderRow(headers, columnWidths, showIndexes, indexWidth)
                dispatch(null, Delegate(tag, headersRow))
                val headerBottom = getHeaderBottomRow(columnWidths)
                dispatch(null, Delegate(tag, headerBottom))
            }

            // Output data
            data.map { interpreter(it) }.forEachIndexed { index, it ->
                val row = buildRow(it, columnWidths, showIndexes, indexWidth, index)
                dispatch(null, Delegate(tag, row))
            }
            // Output bottom row
            dispatch(null, Delegate(tag, buildString {
                outputRow("┗", "┻", "━", "┛", columnWidths)
            }))
            return@runBlocking
        }
    }

    /*
     JSON
     */
    private fun doJson(tag: String?, json: String) {

        try {
            val element = JsonParser().parse(json)
            val obj = JSONObject(json).toString(2)
            surroundWithBorder(splitByLine(obj)).forEach {
                //                dispatch(options, tag ?: getTag(""), it)
                dispatch(null, Delegate(tag ?: "", it))
            }

        } catch (e: JSONException) {
            log("Invalid JSON", e)
        }
    }

    /*
     XML
     */
    private fun doXml(tag: String, xml: String) {
        try {
            val source = StreamSource(xml.reader())
            val result = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(source, result)
            val lines =
                surroundWithBorder(splitByLine(result.writer.toString().replaceFirst(">", ">\n"))).toMutableList()
            lines.removeAt(lines.lastIndex - 1) // It always had an extra line at the end
            lines.forEach {
                //                dispatch(options, tag, it)
                dispatch(null, Delegate(tag, it))
            }
        } catch (e: TransformerException) {
            log("Invalid XML", e)
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

    private suspend fun buildRow(data: List<String>, columnWidths: List<Int>, showIndexes: Boolean, indexWidth: Int, index: Int?): String {
        return coroutineScope {
            withContext(Dispatchers.Default) {
                val indexRes = async {
                    if (showIndexes) {
                        if (index == null) {
                            " ".repeat(indexWidth).plus("┃")
                        } else {
                            buildString {
                                applyPadding("$index", indexWidth, Alignment.RIGHT)
                                append("┃")
                            }
                        }
                    } else ""
                }
                val concatenated = async {
                    val builder = StringBuilder()
                    data.forEachIndexed { i, it ->
                        builder.applyPadding(it, columnWidths[i + showIndexes.toInt()])
                            .append("┃")
                    }
                    builder.toString()
                }
                "┃${indexRes.await()}${concatenated.await()}"
            }
        }
    }

    private suspend fun getHeaderBottomRow(columnWidths: List<Int>): String {
        return coroutineScope {
            withContext(Dispatchers.Default) {
                StringBuilder().outputRow("┣", "╋", "━", "┫", columnWidths).toString()
            }
        }
    }

    private suspend fun StringBuilder.applyPadding(
        string: String,
        columnWidth: Int,
        alignment: Alignment = Alignment.CENTER
    ): StringBuilder {
        return coroutineScope {
            val padding = calcPadding(string, columnWidth, alignment)
            val start = withContext(Dispatchers.Default) { " " * padding.first }
            val end = withContext(Dispatchers.Default) { " " * padding.second }
            this@applyPadding.append("$start$string$end")
        }

    }

    private operator fun String.times(value: Int): String {
        return " ".repeat(value)
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