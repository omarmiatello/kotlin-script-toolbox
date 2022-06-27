package com.github.omarmiatello.kotlinscripttoolbox.telegram

import com.github.omarmiatello.kotlinscripttoolbox.core.CacheKey
import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.github.omarmiatello.telegram.ParseMode
import com.github.omarmiatello.telegram.TelegramClient

object TelegramKey {
    val client = CacheKey<TelegramClient>()
    val defaultChatId = CacheKey<String>()
}

fun KotlinScriptToolboxScope.setupTelegram(
    apiKey: String,
    defaultChatId: String,
) {
    cache.put(TelegramKey.client, TelegramClient(apiKey = apiKey))
    cache.put(TelegramKey.defaultChatId, defaultChatId)
}

val KotlinScriptToolboxScope.telegramClient: TelegramClient
    get() = cache.getOrNull(TelegramKey.client) ?: error("Have you run setupTelegram()?")
val KotlinScriptToolboxScope.telegramDefaultChatId: String
    get() = cache.getOrNull(TelegramKey.defaultChatId) ?: error("Have you run setupTelegram()?")

suspend fun KotlinScriptToolboxScope.sendTelegramMessage(
    text: String,
    chatId: String = telegramDefaultChatId,
) {
    println("💬 ${text.take(4096)}")
    telegramClient.sendMessage(
        chat_id = chatId,
        text = text.take(4096),
        parse_mode = ParseMode.Markdown,
        disable_web_page_preview = false
    )
}
