package com.github.omarmiatello.kotlinscripttoolbox.example

import com.github.omarmiatello.kotlinscripttoolbox.core.launchKotlinScriptToolbox
import com.github.omarmiatello.kotlinscripttoolbox.gson.readJson
import com.github.omarmiatello.kotlinscripttoolbox.gson.readJsonOrNull
import com.github.omarmiatello.kotlinscripttoolbox.gson.writeJson
import com.github.omarmiatello.kotlinscripttoolbox.telegram.sendTelegramMessage
import com.github.omarmiatello.kotlinscripttoolbox.telegram.sendTelegramMessages
import com.github.omarmiatello.kotlinscripttoolbox.telegram.setupTelegram
import com.github.omarmiatello.kotlinscripttoolbox.twitter.sendTweet
import com.github.omarmiatello.kotlinscripttoolbox.twitter.sendTweets
import com.github.omarmiatello.kotlinscripttoolbox.twitter.setupTwitter
import com.github.omarmiatello.kotlinscripttoolbox.zerosetup.launchKotlinScriptToolboxZeroSetup

fun main() {
    launchKotlinScriptToolbox(
        scriptName = "Test read System Property",
    ) {
        // system env or 'local.properties'
        val secretData = readSystemProperty("SECRET_DATA")
        val secretData2 = readSystemPropertyOrNull("SECRET_DATA_OR_NULL")
        println("System Property 'SECRET_DATA': $secretData")
    }

    launchKotlinScriptToolbox(
        scriptName = "Test write/read file with text",
        filepathPrefix = "example-data/"
    ) {
        // files: write text
        writeText("test1.txt", "Ciao")

        // files: read text
        println("test1.txt: ${readText("test1.txt")}")
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
        println("test2-b.json to object: ${readJson<MyExample>("test2-b.json")}")
    }

    launchKotlinScriptToolbox(
        scriptName = "Test Telegram messages",
    ) {
        setupTelegram(
            apiKey = readSystemProperty("TELEGRAM_BOT_APIKEY"),
            defaultChatId = readSystemProperty("TELEGRAM_CHAT_ID"),
        )
        sendTelegramMessage("My message")
    }

    launchKotlinScriptToolbox(
        scriptName = "Test Twitter messages",
    ) {
        setupTwitter(
            consumerKey = readSystemProperty("TWITTER_CONSUMER_KEY"),
            consumerSecret = readSystemProperty("TWITTER_CONSUMER_SECRET"),
            accessKey = readSystemProperty("TWITTER_ACCESS_KEY"),
            accessSecret = readSystemProperty("TWITTER_ACCESS_SECRET"),
        )
        sendTweet("My tweet")
    }

    launchKotlinScriptToolboxZeroSetup {
        sendTelegramMessage("My message")
        sendTelegramMessages(listOf("My message 1", "My message 2"))
        sendTweet("My tweet")
        sendTweets(listOf("My tweet 1", "My tweet 2"))
    }
}