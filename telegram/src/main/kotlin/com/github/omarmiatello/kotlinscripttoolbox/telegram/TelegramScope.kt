package com.github.omarmiatello.kotlinscripttoolbox.telegram

import com.github.omarmiatello.kotlinscripttoolbox.core.BaseScope
import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.github.omarmiatello.telegram.ParseMode
import com.github.omarmiatello.telegram.TelegramClient

interface TelegramScope : KotlinScriptToolboxScope {
    val telegramClient: TelegramClient
    val telegramDefaultChatIds: List<String>

    suspend fun sendTelegramMessage(
        text: String,
        chatIds: List<String> = telegramDefaultChatIds,
        maxMessages: Int = 1,
    ) {
        sendTelegramMessages(
            texts = text.chunked(MESSAGE_MAX_SIZE).take(maxMessages),
            chatIds = chatIds,
        )
    }

    suspend fun sendTelegramMessage(
        text: String,
        chatId: String,
        maxMessages: Int = 1,
    ) = sendTelegramMessage(text = text, chatIds = listOf(chatId), maxMessages = maxMessages)

    suspend fun sendTelegramMessages(
        texts: List<String>,
        chatIds: List<String> = telegramDefaultChatIds,
    ) {
        chatIds.forEach { chatId ->
            texts.map { it.take(MESSAGE_MAX_SIZE) }
                .forEach { msg ->
                    println("💬 --> $msg")
                    val response = telegramClient.sendMessage(
                        chat_id = chatId,
                        text = msg,
                        parse_mode = ParseMode.Markdown,
                        disable_web_page_preview = false
                    )
                    println("💬 <-- $response")
                }
        }
    }

    companion object {
        const val MESSAGE_MAX_SIZE = 4096

        fun fromDefaults(
            baseScope: BaseScope,
            apiKey: String = baseScope.readSystemProperty("TELEGRAM_BOT_APIKEY"),
            defaultChatIds: List<String> = baseScope.readSystemProperty("TELEGRAM_CHAT_ID_LIST")
                .split(",")
                .map { it.trim() },
        ): TelegramScope = from(
            apiKey = apiKey,
            defaultChatIds = defaultChatIds,
        )

        fun from(
            apiKey: String,
            defaultChatIds: List<String>,
        ): TelegramScope = from(
            client = TelegramClient(apiKey = apiKey),
            defaultChatIds = defaultChatIds
        )

        fun from(
            client: TelegramClient,
            defaultChatIds: List<String>,
        ): TelegramScope = object : TelegramScope {
            override val telegramClient = client
            override val telegramDefaultChatIds = defaultChatIds
        }
    }
}
