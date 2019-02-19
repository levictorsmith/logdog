package me.levansmith.logdog.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import me.levansmith.logdog.library.LogDog
import kotlin.math.absoluteValue
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val LOG_TAG = LogDog.Tag.create(MainActivity::class.java)
    }

    data class POJO(
        val foo: String,
        var bar: Int,
        var baz: Int,
        val qux: Float
    ) {
        companion object {
            val serializer = JsonSerializer<POJO> { src, _, _ ->
                JsonObject().apply {
                    addProperty("foo", src.foo)
                    addProperty("bar", src.bar)
                    addProperty("baz", src.baz)
                    addProperty("qux", src.qux)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogDog.d(LOG_TAG, "Debug message")
        LogDog.timeStart(LOG_TAG, "sample_timer", Log.DEBUG)
        Thread.sleep(3000L)
        LogDog.timeEnd("sample_timer")
        for (i in 1..10) {
            LogDog.count(LOG_TAG, "sample_counter", Log.DEBUG)
        }

        LogDog.v(LOG_TAG, "Log Level: VERBOSE")
        LogDog.d(LOG_TAG, "Log Level: DEBUG")
        LogDog.i(LOG_TAG, "Log Level: INFO")
        LogDog.w(LOG_TAG, "Log Level: WARN")
        LogDog.e(LOG_TAG, "Log Level: ERROR")
        LogDog.wtf(LOG_TAG, "Log Level: ASSERT")

        val data = mutableListOf<POJO>()
        for (i in 1..101) {
            data.add(
                POJO(
                    Random.nextBytes(Random.nextInt(20)).map { ((it % 94).absoluteValue + 33).toByte() }.toByteArray().toString(
                        Charsets.US_ASCII
                    ), Random.nextInt(), Random.nextInt(), Random.nextFloat()
                )
            )
        }
        LogDog.table(LOG_TAG, data, listOf("Foo", "Bar", "Baz", "Quuuuuuuuuuuuuuuuuuuux")) {
            listOf(it.foo, it.bar.toString(), it.baz.toString(), it.qux.toString())
        }

        LogDog.table(LOG_TAG, data.map { it.foo }, listOf("Foo"), false, Log.ERROR) {
            listOf(it)
        }

        // Log an object using JSON format
        LogDog.json(LOG_TAG, "{\"foo\":\"blah\",\"bar\":123, \"baz\":[1,2,3,4,5,6,7]}", Log.DEBUG)

        // Gson Serializer/TypeAdapter friendly
        val example = POJO("foobar", 1234, 56789, 123.456f)
        LogDog.json(LOG_TAG, example, POJO.serializer, Log.WARN)

        // With Auto-tags
        LogDog.v("This is an auto-tag message.")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.count -> {
                LogDog.count(LOG_TAG, "sample_counter", Log.DEBUG)
            }
            R.id.count_reset -> {
                LogDog.countReset("sample_counter")
            }
        }
    }

}
