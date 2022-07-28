package com.github.omarmiatello.kotlinscripttoolbox.telegram

import com.github.omarmiatello.kotlinscripttoolbox.core.BaseScope
import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.github.omarmiatello.kotlinscripttoolbox.core.Messages
import com.github.omarmiatello.telegram.*

interface TelegramScope : KotlinScriptToolboxScope {
    val telegramClient: TelegramClient
    val telegramDefaultChatIds: List<String>

    suspend fun <T> sendTelegramRequests(
        texts: List<String>,
        chatIds: List<String> = telegramDefaultChatIds,
        request: suspend (chatId: String, msg: String) -> TelegramResponse<T>,
    ) {
        chatIds.forEach { chatId ->
            texts.map { it.take(MESSAGE_MAX_SIZE) }
                .forEach { msg ->
                    println("💬 --> $msg")
                    val response = request(chatId, msg)
                    println("💬 <-- $response")
                }
        }
    }

    suspend fun sendTelegramMessages(
        texts: List<String>,
        chatIds: List<String> = telegramDefaultChatIds,
        parse_mode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disable_web_page_preview: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_to_message_id: Long? = null,
        allow_sending_without_reply: Boolean? = null,
        reply_markup: KeyboardOption? = null,
    ) = sendTelegramRequests(
        texts = texts,
        chatIds = chatIds,
        request = { chatId, msg ->
            telegramClient.sendMessage(
                chat_id = chatId,
                text = msg,
                parse_mode = parse_mode,
                entities = entities,
                disable_web_page_preview = disable_web_page_preview,
                disable_notification = disable_notification,
                protect_content = protect_content,
                reply_to_message_id = reply_to_message_id,
                allow_sending_without_reply = allow_sending_without_reply,
                reply_markup = reply_markup,
            )
        }
    )

    suspend fun sendTelegramMessage(
        text: String,
        chatIds: List<String> = telegramDefaultChatIds,
        maxMessages: Int = 1,
        parse_mode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disable_web_page_preview: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_to_message_id: Long? = null,
        allow_sending_without_reply: Boolean? = null,
        reply_markup: KeyboardOption? = null,
    ) = sendTelegramMessages(
        texts = text.chunked(MESSAGE_MAX_SIZE).take(maxMessages),
        chatIds = chatIds,
        parse_mode = parse_mode,
        entities = entities,
        disable_web_page_preview = disable_web_page_preview,
        disable_notification = disable_notification,
        protect_content = protect_content,
        reply_to_message_id = reply_to_message_id,
        allow_sending_without_reply = allow_sending_without_reply,
        reply_markup = reply_markup,
    )

    suspend fun sendTelegramMessage(
        text: String,
        chatId: String,
        maxMessages: Int = 1,
        parse_mode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disable_web_page_preview: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_to_message_id: Long? = null,
        allow_sending_without_reply: Boolean? = null,
        reply_markup: KeyboardOption? = null,
    ) = sendTelegramMessage(
        text = text,
        chatIds = listOf(chatId),
        maxMessages = maxMessages,
        parse_mode = parse_mode,
        entities = entities,
        disable_web_page_preview = disable_web_page_preview,
        disable_notification = disable_notification,
        protect_content = protect_content,
        reply_to_message_id = reply_to_message_id,
        allow_sending_without_reply = allow_sending_without_reply,
        reply_markup = reply_markup,
    )

    suspend fun <T> sendTelegramRequests(
        messages: Messages,
        chatIds: List<String> = telegramDefaultChatIds,
        request: suspend (chatId: String, msg: String) -> TelegramResponse<T>,
    ) = sendTelegramRequests(
        texts = messages.toStrings(MESSAGE_MAX_SIZE),
        chatIds = chatIds,
        request = request,
    )

    suspend fun sendTelegramMessages(
        messages: Messages,
        chatIds: List<String> = telegramDefaultChatIds,
        parse_mode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        disable_web_page_preview: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_to_message_id: Long? = null,
        allow_sending_without_reply: Boolean? = null,
        reply_markup: KeyboardOption? = null,
    ) = sendTelegramMessages(
        texts = messages.toStrings(MESSAGE_MAX_SIZE),
        chatIds = chatIds,
        parse_mode = parse_mode,
        entities = entities,
        disable_web_page_preview = disable_web_page_preview,
        disable_notification = disable_notification,
        protect_content = protect_content,
        reply_to_message_id = reply_to_message_id,
        allow_sending_without_reply = allow_sending_without_reply,
        reply_markup = reply_markup,
    )

    companion object {
        const val MESSAGE_MAX_SIZE = 4096

        fun from(
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
