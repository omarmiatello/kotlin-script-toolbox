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
    val cache = Cache()
    val localProperties: Properties? = try {
        Properties().also { it.load(File(filepathLocalProperties).inputStream()) }
    } catch (e: Exception) {
        null
    }

    fun readSystemPropertyOrNull(propertyName: String): String? =
        System.getenv(propertyName) ?: localProperties?.getProperty(propertyName)

    fun readSystemProperty(propertyName: String): String =
        readSystemPropertyOrNull(propertyName)
            ?: error("Property not found: $propertyName")

    fun writeText(pathname: String, text: String): Unit =
        File("$filepathPrefix$pathname")
            .also { if (it.parentFile?.exists() == false) it.parentFile.mkdirs() }
            .writeText(text)

    fun readTextOrNull(pathname: String): String? =
        File("$filepathPrefix$pathname")
            .takeIf { it.exists() }
            ?.readText()

    fun readText(pathname: String): String =
        readTextOrNull(pathname)
            ?: error("File not found: $pathname")

}

class CacheKey<T>

class Cache {

    private val map = mutableMapOf<CacheKey<*>, Any>()

    fun <T> getOrNull(key: CacheKey<T>): T? =
        map[key] as T?

    fun <T : Any> get(key: CacheKey<T>): T =
        getOrNull(key) ?: error("Key not found: $key")

    fun <T : Any> put(key: CacheKey<T>, value: T) {
        map[key] = value
    }
}
