package com.github.omarmiatello.kotlinscripttoolbox.core

import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

fun launchKotlinScriptToolbox(
    scriptName: String,
    showDuration: Boolean = true,
    filepathPrefix: String = "",
    filepathLocalProperties: String = "local.properties",
    block: suspend KotlinScriptToolboxScope.() -> Unit,
): KotlinScriptToolboxScope {
    if (showDuration) println("🏁 $scriptName - Start!")
    val startTime = System.currentTimeMillis()
    val scope = KotlinScriptToolboxScope(
        filepathPrefix = filepathPrefix,
        filepathLocalProperties = filepathLocalProperties,
    )
    runBlocking { block(scope) }
    if (showDuration) println("🎉 $scriptName - Completed in ${System.currentTimeMillis() - startTime}ms\n")
    return scope
}


class KotlinScriptToolboxScope(
    val filepathPrefix: String = "",
    val filepathLocalProperties: String = "local.properties",
) {
    val localProperties: Properties? = try {
        Properties().also { it.load(File(filepathLocalProperties).inputStream()) }
    } catch (e: Exception) {
        null
    }

    fun readSystemPropertyOrNull(propertyName: String): String? {
        return System.getenv(propertyName) ?: localProperties?.getProperty(propertyName)
    }

    fun writeText(pathname: String, text: String): Unit =
        File("$filepathPrefix$pathname")
            .also { if (it.parentFile?.exists() == false) it.parentFile.mkdirs() }
            .writeText(text)

    fun readTextOrNull(pathname: String): String? =
        File("$filepathPrefix$pathname")
            .takeIf { it.exists() }
            ?.readText()

}