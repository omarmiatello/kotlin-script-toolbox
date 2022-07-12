package com.github.omarmiatello.kotlinscripttoolbox.example

import com.github.omarmiatello.kotlinscripttoolbox.core.*
import com.github.omarmiatello.kotlinscripttoolbox.gson.readJson
import com.github.omarmiatello.kotlinscripttoolbox.gson.readJsonOrNull
import com.github.omarmiatello.kotlinscripttoolbox.gson.writeJson
import com.github.omarmiatello.kotlinscripttoolbox.telegram.TelegramScope
import com.github.omarmiatello.kotlinscripttoolbox.twitter.TwitterScope
import com.github.omarmiatello.kotlinscripttoolbox.zerosetup.ZeroSetupScope


fun main() {
    launchKotlinScriptToolbox(
        scope = BaseScope.from(),
        scriptName = "Test read System Property",
    ) {
        // system env or 'local.properties'
        val secretData = readSystemProperty("SECRET_DATA")
        val secretData2 = readSystemPropertyOrNull("SECRET_DATA_OR_NULL")
        println("System Property 'SECRET_DATA': $secretData")
    }

    launchKotlinScriptToolbox(
        scope = BaseScope.from(filepathPrefix = "example-data/"),
        scriptName = "Test write/read file with text",
    ) {
        // files: write text
        writeText("test1.txt", "Ciao")

        // files: read text
        println("test1.txt: ${readText("test1.txt")}")
    }

    launchKotlinScriptToolbox(
        scope = BaseScope.from(filepathPrefix = "example-data/"),
        scriptName = "Test write/read file with objects (serialized as json)",
    ) {
        data class MyExample(val p1: String, val p2: Int? = null)

        // files: write json objects
        writeJson("test2-a.json", MyExample(p1 = "test1"))
        writeJson("test2-b.json", MyExample(p1 = "test2", p2 = 3))

        // files: read json objects
        println("test2-a.json to object: ${readJsonOrNull<MyExample>("test2-a.json")}")
        println("test2-b.json to object: ${readJson<MyExample>("test2-b.json")}")
    }

    launchKotlinScriptToolbox(
        scope = TelegramScope.from(apiKey = "my api key", defaultChatIds = listOf("123321")),
        scriptName = "Test Telegram messages",
    ) {
        sendTelegramMessage("My message")
    }

    launchKotlinScriptToolbox(
        scope = TwitterScope.from(baseScope = BaseScope.from()),
        scriptName = "Test Twitter messages",
    ) {
        sendTweet("My tweet")
    }

    val baseScope = BaseScope.from()
    launchKotlinScriptToolbox(
        scope = object : BaseScope by baseScope,
            TelegramScope by TelegramScope.from(baseScope),
            TwitterScope by TwitterScope.from(baseScope) {},
        scriptName = "Setup using defaults"
    ) {
        sendTelegramMessage("My message")
        sendTelegramMessages(listOf("My message 1", "My message 2"))
        sendTweet("My tweet")
        sendTweets(listOf("My tweet 1", "My tweet 2"))
    }

    launchKotlinScriptToolbox(ZeroSetupScope()) {
        sendTelegramMessage("My message")
        sendTelegramMessages(listOf("My message 1", "My message 2"))
        sendTweet("My tweet")
        sendTweets(listOf("My tweet 1", "My tweet 2"))
    }
}