package com.github.omarmiatello.kotlinscripttoolbox.core

data class Messages(val content: List<MessageContentL1>) {
    fun toStrings(maxLength: Int): List<String> = buildList {
        var remains = maxLength
        val stringBuilder = StringBuilder()

        content.forEachIndexed { index, l1 ->
            val canAppend = if (index != 0) {
                remains -= l1.joinL1.length
                when (l1.appendIf) {
                    AppendIfNotDivide -> remains >= l1.messageSize
                    AppendIfRoom -> true
                    AppendNever -> false
                }
            } else false

            if (!canAppend) {
                if (stringBuilder.isNotEmpty()) {
                    add(stringBuilder.toString())
                }
                stringBuilder.clear()
                remains = maxLength
            }

            val strings = l1.toStrings(maxLengthFirst = remains, maxLengthFollowing = maxLength)

            if (strings.isNotEmpty()) {
                if (canAppend && strings.first().isNotEmpty()) {
                    stringBuilder.append(l1.joinL1)
                }

                val first = strings.first()
                stringBuilder.append(first)
                if (strings.size == 1) {
                    remains -= first.length
                } else {
                    val last = strings.last()
                    remains = maxLength - last.length
                    add(stringBuilder.toString())
                    stringBuilder.clear()
                    stringBuilder.append(last)
                    addAll(strings.drop(1).dropLast(1))
                }
            }
        }

        if (stringBuilder.isNotEmpty()) {
            add(stringBuilder.toString())
        }
    }
}

sealed interface MessageContent {
    val messageSize: Int
    fun toStrings(maxLengthFirst: Int, maxLengthFollowing: Int = maxLengthFirst): List<String>
}

data class MessageContentL1(
    val leafs: List<MessageContentL2>,
    val appendIf: MessageAppendStrategy = AppendIfNotDivide,
    val joinL1: String = "\n",
    val joinL2: String = "",
    override val messageSize: Int = leafs.sumOf { it.messageSize } + joinL2.length * leafs.lastIndex,
) : MessageContent {
    override fun toStrings(maxLengthFirst: Int, maxLengthFollowing: Int) = buildList {
        var remains = maxLengthFirst
        val stringBuilder = StringBuilder()

        leafs.forEachIndexed { index, l2 ->
            if (index != 0) {
                remains -= joinL2.length
            }
            val strings = l2.toStrings(remains, maxLengthFollowing)
            if (strings.isNotEmpty()) {
                if (index != 0) {
                    stringBuilder.append(joinL2)
                }
                val first = strings.first()
                stringBuilder.append(first)
                if (strings.size == 1) {
                    remains -= first.length
                } else {
                    val last = strings.last()
                    remains = maxLengthFollowing - last.length
                    add(stringBuilder.toString())
                    stringBuilder.clear()
                    stringBuilder.append(last)
                    addAll(strings.drop(1).dropLast(1))
                }
            }
        }

        if (stringBuilder.isNotEmpty()) {
            add(stringBuilder.toString())
        }
    }
}

sealed interface MessageContentL2 : MessageContent

data class MessageText(
    val text: String,
    val breakStrategy: MessageBreakStrategy = BreakOnChar(),
    override val messageSize: Int = text.length,
) : MessageContentL2 {
    override fun toStrings(maxLengthFirst: Int, maxLengthFollowing: Int) =
        breakStrategy.toStrings(text, messageSize, maxLengthFirst, maxLengthFollowing)
}

data class MessageUrl(
    val text: String,
    val fallback: MessageBreakFallback = ThrowError,
    override val messageSize: Int = text.length,
) : MessageContentL2 {
    val breakStrategy = BreakOnLastContent(fallback)

    override fun toStrings(maxLengthFirst: Int, maxLengthFollowing: Int) =
        breakStrategy.toStrings(text, messageSize, maxLengthFirst, maxLengthFollowing)
}

@DslMarker
annotation class MessageContentMarker

@MessageContentMarker
sealed interface MessageScope

class MessagesScope : MessageScope {
    internal val content = mutableListOf<MessageContentL1>()
    fun addMessage(
        appendIf: MessageAppendStrategy = AppendIfNotDivide,
        joinGroups: String = "\n",
        joinLeafs: String = "",
        block: MessageL1Scope.() -> Unit,
    ) = content.add(
        MessageContentL1(
            leafs = MessageL1Scope().also(block).content,
            appendIf = appendIf,
            joinL1 = joinGroups,
            joinL2 = joinLeafs,
        )
    )
}

class MessageL1Scope : MessageScope {
    internal val content = mutableListOf<MessageContentL2>()
    fun text(
        message: String,
        breakStrategy: MessageBreakStrategy = BreakOnChar(),
        messageSize: Int = message.length,
    ) = content.add(
        MessageText(
            text = message,
            breakStrategy = breakStrategy,
            messageSize = messageSize,
        )
    )

    fun url(
        url: String,
        fallback: MessageBreakFallback = ThrowError,
        messageSize: Int = url.length
    ) = content.add(
        MessageUrl(
            text = url,
            fallback = fallback,
            messageSize = messageSize,
        )
    )
}

fun buildMessages(block: MessagesScope.() -> Unit): Messages =
    Messages(MessagesScope().also(block).content)

