package com.github.omarmiatello.kotlinscripttoolbox.zerosetup

import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.github.omarmiatello.kotlinscripttoolbox.core.launchKotlinScriptToolbox
import com.github.omarmiatello.kotlinscripttoolbox.telegram.setupTelegram
import com.github.omarmiatello.kotlinscripttoolbox.twitter.setupTwitter

fun launchKotlinScriptToolboxZeroSetup(
    scriptName: String = "Zero setup script",
    showDuration: Boolean = true,
    filepathPrefix: String = "",
    filepathLocalProperties: String = "local.properties",
    block: suspend KotlinScriptToolboxScope.() -> Unit,
) = launchKotlinScriptToolbox(
    scriptName = scriptName,
    showDuration = showDuration,
    filepathPrefix = filepathPrefix,
    filepathLocalProperties = filepathLocalProperties,
    block = {
        try {
            setupTelegram(
                apiKey = readSystemProperty("TELEGRAM_BOT_APIKEY"),
                defaultChatId = readSystemProperty("TELEGRAM_CHAT_ID"),
            )
        } catch (e: Exception) {
            println("🛑 setupTelegram() failed, have you set TELEGRAM_BOT_APIKEY and TELEGRAM_CHAT_ID?")
        }
        try {
            setupTwitter(
                consumerKey = readSystemProperty("TWITTER_CONSUMER_KEY"),
                consumerSecret = readSystemProperty("TWITTER_CONSUMER_SECRET"),
                accessKey = readSystemProperty("TWITTER_ACCESS_KEY"),
                accessSecret = readSystemProperty("TWITTER_ACCESS_SECRET"),
            )
        } catch (e: Exception) {
            println("🛑 setupTwitter() failed, have you set TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_ACCESS_KEY, TWITTER_ACCESS_SECRET?")
        }

        block()
    },
)