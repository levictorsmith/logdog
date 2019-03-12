package me.levansmith.logdog.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import me.levansmith.logdog.library.AnalyticsEvent
import me.levansmith.logdog.library.LogDog
import me.levansmith.logdog.library.LogDogConfig
import kotlin.math.absoluteValue
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val LOG_TAG = LogDog.Tag.create(MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keeping time
        LogDog.timeStart(LOG_TAG, "sample_timer")
        Thread.sleep(3000L)
        LogDog.timeEnd("sample_timer")

        // Keeping count
        for (i in 1..10) {
            LogDog.count(LOG_TAG, "sample_counter")
        }

        /*
         Logging Levels
         */

        LogDog.v(LOG_TAG, "Log Level: VERBOSE")
        LogDog.d(LOG_TAG, "Log Level: DEBUG")
        LogDog.i(LOG_TAG, "Log Level: INFO")
        LogDog.w(LOG_TAG, "Log Level: WARN")
        LogDog.e(LOG_TAG, "Log Level: ERROR")
        LogDog.wtf(LOG_TAG, "Log Level: ASSERT")

        // This is a generic log which defaults to VERBOSE level. Kinda useless IMO
        LogDog.log(LOG_TAG, "This will default to VERBOSE")
        // You can prepend the log level you want to log in
        LogDog.e.log("This will log an ERROR message")
        // If you try to configure the logging level, it will use the level mentioned last
        LogDog.v.d.i.w.e.wtf.d(LOG_TAG, "This will be a DEBUG message, since it takes the highest precedence, being the last")
        LogDog.wtf.w.log(LOG_TAG, "This will be a WARN level message")
        LogDog.e
        LogDog.log(LOG_TAG, "This will log at a VERBOSE level because the previous line has no effect on the current line")

        /*
         Outputting data
         */

        // Output a table of data, given the "interpreter" for what goes in which column
        val data = createObjects()
        LogDog.table(LOG_TAG, data, listOf("Foo", "Bar", "Baz", "Quuuuuuuuuuuuuuuuuuuux")) {
            listOf(it.foo, it.bar.toString(), it.baz.toString(), it.qux.toString())
        }

        // Just one column without indexes
        LogDog.e.table(LOG_TAG, data.map { it.foo }, listOf("Foo"), false) {
            listOf(it)
        }

        // Log an object using JSON format
        LogDog.d.json(LOG_TAG, "{\"foo\":\"blah\",\"bar\":123, \"baz\":[1,2,3,4,5,6,7]}")

        // Gson Serializer/TypeAdapter friendly
        val example = POJO("foobar", 1234, 56789, 123.456f)
        LogDog.w.json(LOG_TAG, example, POJO.serializer)

        // Log an object using XML format
        LogDog.d.xml(LOG_TAG, "<parent><child>I am a child</child><child with-attribute=\"hey there\">This has content</child></parent>")

        /*
         Cool Features
         */

        // With Auto-tags. All methods have the option to use auto-tags!
        LogDog.v("This is an auto-tag message.")

        // Disabling messages
        LogDog.hide.log("This is a hidden message, which you'll never see")

        LogDogConfig.disableLogs = true
        // All logs after this will be disabled
        LogDog.d("You'll never see me!! I'm invisible Barney!")
        LogDog.force.d("This defies all preventions and logs anyway.")
        LogDog.d("You'll never see me etiher!!")
        LogDog.hide.log("Hidden messages are hidden, just as long cat is long")
        LogDogConfig.disableLogs = false
        LogDog.hide.log("This is a hidden message, despite logs being enabled again")
        // Force always takes precedence and will always output the log
        LogDog.force.hide.hide.hide.log("I'm sorry Dave, I'm afraid I can't do that.")

        LogDogConfig.logThreshold = Log.ERROR
        // All logs below the specified threshold will be hidden
        LogDog.v("This will be hidden")
        LogDog.d("This too")
        LogDog.i("This too")
        LogDog.w("This too")
        LogDog.v.log("These will also be hidden")
        LogDog.d.log("These will also be hidden")
        LogDog.i.log("These will also be hidden")
        LogDog.w.log("These will also be hidden")
        LogDog.e("However, this one will be displayed")
        LogDog.wtf("And this one too")
        LogDogConfig.logThreshold = Log.VERBOSE

        // If you want to auto-send a tag, message and/or arguments to your preferred analytics manager, this option will let you
        LogDog.send.d("This message will send (if possible) the given data to the configured analytics service.")
        val event = SampleEvent("John Jacob", "Jingleheimerschmidt", 72, 3.8f)
        LogDog.send.d(event)

        /*
         Although combining multiple log statements are possible and act as suspected, it is bad practice
         and will probably yield unexpected results.
         */
        LogDog.hide.e(LogDog.w("This is bad practice, but still possible").toString())
        LogDog.hide.e(LogDog.force.w("This is bad practice, but still possible").toString())
        LogDog.e(LogDog.hide.w("This is bad practice, but still possible").toString())
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.count -> {
                LogDog.d.count(LOG_TAG, "sample_counter")
                // Send a button click event
                val event = CountButtonClickEvent(
                    LogDog.getCount("sample_counter")
                )
                LogDog.send.d(event)
            }
            R.id.count_reset -> {
                LogDog.d.countReset(LOG_TAG, "sample_counter")
            }
        }
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

    data class SampleEvent(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val GPA: Float
    ) : AnalyticsEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {

        // If you want to include standardized keys (like in Firebase), just provide the keys in the getFields function
        override fun getFields() = mutableMapOf<String, String>().apply {
            put(FirebaseAnalytics.Param.ITEM_NAME, "Sample Event")
            put(FirebaseAnalytics.Param.ITEM_ID, "sample_event_id")
            put("first_name", firstName)
            put("last_name", lastName)
            put("age", age.toString())
            put("GPA", GPA.toString())
        }
    }

    data class CountButtonClickEvent(val count: Long) : AnalyticsEvent("increment_counter") {

        override fun getFields() = mutableMapOf<String,String>().apply {
            put("count", count.toString())
        }
    }

    private fun createObjects(): List<POJO> = (1..101).map {
        POJO(
            Random.nextBytes(Random.nextInt(20)).map { ((it % 94).absoluteValue + 33).toByte() }.toByteArray().toString(
                Charsets.US_ASCII
            ), Random.nextInt(), Random.nextInt(), Random.nextFloat()
        )
    }

}
