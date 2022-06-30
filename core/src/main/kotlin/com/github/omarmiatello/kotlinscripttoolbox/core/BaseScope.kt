package com.github.omarmiatello.kotlinscripttoolbox.core

import java.io.File
import java.util.*

interface BaseScope : KotlinScriptToolboxScope {
    val filepathPrefix: String
    val filepathLocalProperties: String
    val cache: Cache
    val localProperties: Properties?

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

    companion object {
        fun fromDefaults(
            filepathPrefix: String = "",
            filepathLocalProperties: String = "local.properties",
            cache: Cache = Cache(),
            localProperties: Properties? = try {
                Properties().also { it.load(File(filepathLocalProperties).inputStream()) }
            } catch (e: Exception) {
                null
            },
        ): BaseScope = object : BaseScope {
            override val filepathPrefix = filepathPrefix
            override val filepathLocalProperties = filepathLocalProperties
            override val cache = cache
            override val localProperties = localProperties
        }
    }
}
