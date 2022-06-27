package com.github.omarmiatello.kotlinscripttoolbox.zerosetup

import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.google.gson.Gson

val _gson = Gson().newBuilder().setPrettyPrinting().create()

val KotlinScriptToolboxScope.gson: Gson get() = _gson

inline fun <reified T> T.toJson(): String = _gson.toJson(this, T::class.java)
inline fun <reified T> String.fromJson(): T = _gson.fromJson(this, T::class.java)

inline fun <reified T : Any> KotlinScriptToolboxScope.writeJson(
    pathname: String,
    obj: T,
) = writeText(
    pathname = pathname,
    text = obj.toJson(),
)

inline fun <reified T> KotlinScriptToolboxScope.readJsonOrNull(
    pathname: String,
): T? = try {
    readTextOrNull(pathname = pathname)?.fromJson()
} catch (e: Exception) {
    null
}

inline fun <reified T> KotlinScriptToolboxScope.readJson(
    pathname: String,
): T = readJsonOrNull(pathname) ?: error("Fail to read or parse: $pathname")
