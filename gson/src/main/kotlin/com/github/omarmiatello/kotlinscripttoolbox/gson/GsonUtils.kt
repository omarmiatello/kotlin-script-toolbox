package com.github.omarmiatello.kotlinscripttoolbox.gson

import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.google.gson.Gson

val gson = Gson().newBuilder().setPrettyPrinting().create()

inline fun <reified T> T.toJson(): String = gson.toJson(this, T::class.java)
inline fun <reified T> String.fromJson(): T = gson.fromJson(this, T::class.java)

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
