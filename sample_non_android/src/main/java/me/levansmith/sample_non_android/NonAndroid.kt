import me.levansmith.logging.*
import me.levansmith.sample_non_android.Logger
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


fun main(args: Array<String>) {
    LogDogConfig.tagGenerator = {
        val formatter = DateTimeFormatter.ISO_INSTANT
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())
        "${formatter.format(Instant.now())}: GENERIC TAG"
    }
    Logger.timeStart("sample_timer")
    Logger.g.log("This is a message")
    Logger.hide.log("Hidden message")
    Logger.force.hide.showThread.log("CUSTOM_TAG", "Have a cookie!")
    Logger.b.log("An error!", Exception("This is an error message!"))
    Logger.timeEnd("sample_timer")
}