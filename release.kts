import java.io.FileInputStream
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

val PROPERTIES_FILE_NAME = "./gradle.properties"
val PROPERTY_VERSION_NAME = "VERSION_NAME"
val PROPERTY_VERSION_CODE = "VERSION_CODE"

fun exec(command: String, workingDir: File? = null, pipe: Boolean = false): String? {
    val redirect = if (pipe) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT
    val process = try {
        ProcessBuilder("/bin/sh", "-c", command)
            .directory(workingDir)
            .redirectOutput(redirect)
            .redirectError(redirect)
            .start()
            .apply { waitFor(60, TimeUnit.MINUTES) }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
    return if (pipe && process != null) {
        return try {
            process
                .inputStream.bufferedReader()
                .readText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    } else null
}

enum class VersionType {
    MAJOR,
    MINOR,
    PATCH
}

fun incrementVersionCode(properties: Properties): Int {
    val versionCode = properties.getProperty(PROPERTY_VERSION_CODE)!!
    return versionCode.toInt() + 1
}

fun incrementVersionName(properties: Properties, type: VersionType): String {
    val versionName = properties.getProperty(PROPERTY_VERSION_NAME)!!
    return versionName.split('.').map { it.toInt() }.toMutableList().let {
        it[type.ordinal]++
        it.joinToString(".")
    }
}

fun incrementVersion(type: VersionType) {
    val properties = object : Properties() {
        override fun keys() = Collections.enumeration(TreeSet<Any>(super.keys))
    }
    val propertiesFile = File(PROPERTIES_FILE_NAME)
    propertiesFile.inputStream().let {
        properties.load(it)
    }

    val versionName = incrementVersionName(properties, type)
    properties.setProperty(PROPERTY_VERSION_NAME, versionName)

    val versionCode = incrementVersionCode(properties).toString()
    properties.setProperty(PROPERTY_VERSION_CODE, versionCode)

    properties.store(propertiesFile.outputStream(), null)
    println("Success. Version is now: v$versionName, CODE: $versionCode")
    println("Adding...")
    exec("git add gradle.properties")
    println("Committing...")
    exec("git commit -m \"Inc v$versionName\" -- $PROPERTIES_FILE_NAME")
}

print("Enter upgrade type [major, minor, patch]: ")
val option = readLine()?.toLowerCase()
val type = when(option) {
    "major" -> VersionType.MAJOR
    "minor" -> VersionType.MINOR
    "patch" -> VersionType.PATCH
    else -> throw Exception("Invalid option")
}
incrementVersion(type)
