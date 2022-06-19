package com.github.omarmiatello.kotlinscripttoolbox.example

import com.github.omarmiatello.kotlinscripttoolbox.core.launchKotlinScriptToolbox
import com.github.omarmiatello.kotlinscripttoolbox.zerosetup.readJsonOrNull
import com.github.omarmiatello.kotlinscripttoolbox.zerosetup.writeJson

fun main() {
    launchKotlinScriptToolbox(
        scriptName = "Test read System Property",
    ) {
        // system env or 'local.properties'
        val secretData = readSystemPropertyOrNull("SECRET_DATA")
        println("System Property 'SECRET_DATA': $secretData")
    }

    launchKotlinScriptToolbox(
        scriptName = "Test write/read file with text",
        filepathPrefix = "example-data/"
    ) {
        // files: write text
        writeText("test1.txt", "Ciao")

        // files: read text
        println("test1.txt: ${readTextOrNull("test1.txt")}")
    }

    launchKotlinScriptToolbox(
        scriptName = "Test write/read file with objects (serialized as json)",
        filepathPrefix = "example-data/",
    ) {
        data class MyExample(val p1: String, val p2: Int? = null)

        // files: write json objects
        writeJson("test2-a.json", MyExample(p1 = "test1"))
        writeJson("test2-b.json", MyExample(p1 = "test2", p2 = 3))

        // files: read json objects
        println("test2-a.json to object: ${readJsonOrNull<MyExample>("test2-a.json")}")
        println("test2-b.json to object: ${readJsonOrNull<MyExample>("test2-b.json")}")
    }
}