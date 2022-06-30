package com.github.omarmiatello.kotlinscripttoolbox.core

import kotlinx.coroutines.runBlocking

interface KotlinScriptToolboxScope

fun <T : KotlinScriptToolboxScope> launchKotlinScriptToolbox(
    scope: T,
    scriptName: String = "Kotlin Script Toolbox",
    showDuration: Boolean = true,
    block: suspend T.() -> Unit,
): T {
    if (showDuration) println("🏁 $scriptName - Start!")
    val startTime = System.currentTimeMillis()
    runBlocking { block(scope) }
    if (showDuration) println("🎉 $scriptName - Completed in ${System.currentTimeMillis() - startTime}ms\n")
    return scope
}
