package com.github.omarmiatello.kotlinscripttoolbox.zerosetup

import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.google.gson.Gson
import java.lang.Exception

private val gson = Gson().newBuilder().setPrettyPrinting().create()

val KotlinScriptToolboxScope.gson: Gson get() = com.github.omarmiatello.kotlinscripttoolbox.zerosetup.gson

inline fun <reified T : Any> KotlinScriptToolboxScope.writeJson(
    pathname: String,
    obj: T,
) = writeText(
    pathname = pathname,
    text = gson.toJson(obj, T::class.java),
)

inline fun <reified T> KotlinScriptToolboxScope.readJsonOrNull(
    pathname: String,
): T? = try {
    gson.fromJson(readTextOrNull(pathname = pathname), T::class.java)
} catch (e: Exception) {
    null
}
