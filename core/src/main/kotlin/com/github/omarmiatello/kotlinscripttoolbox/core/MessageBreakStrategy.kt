package com.github.omarmiatello.kotlinscripttoolbox.core

sealed interface MessageBreakStrategy {
    val fallback: MessageBreakFallback

    fun toStrings(
        text: String,
        messageSize: Int = text.length,
        maxLengthFirst: Int = messageSize,
        maxLengthFollowing: Int = maxLengthFirst,
    ): List<String> {
        if (messageSize <= maxLengthFirst) return listOf(text)

        try {
            return splitText(
                text = text,
                messageSize = messageSize,
                maxLengthFirst = maxLengthFirst,
                maxLengthFollowing = maxLengthFollowing,
            )
        } catch (e: Exception) {
            // splitText() fails - use fallback strategy
        }

        return when (fallback) {
            ThrowError -> error("Fallback strategy: Cannot break $messageSize/$maxLengthFirst: $text")
            Skip -> emptyList()
            Crop -> listOf(text.take(n = maxLengthFirst))
            Split -> listOf(text.take(n = maxLengthFirst)) + text.drop(n = maxLengthFirst)
                .chunked(size = maxLengthFollowing)
        }
    }

    fun splitText(
        text: String,
        messageSize: Int,
        maxLengthFirst: Int,
        maxLengthFollowing: Int,
    ): List<String>
}

data class BreakOnChar(
    val char: Char = '\n',
    override val fallback: MessageBreakFallback = ThrowError,
) : MessageBreakStrategy {
    override fun splitText(text: String, messageSize: Int, maxLengthFirst: Int, maxLengthFollowing: Int) =
        buildList {
            var remain = maxLengthFirst
            val textBuilder = StringBuilder()
            text.split(char).forEachIndexed { index, token ->
                if (token.length <= remain) {
                    if (index != 0) {
                        textBuilder.append(char)
                    }
                    textBuilder.append(token)
                    remain -= token.length + 1
                } else {
                    add(textBuilder.toString())
                    textBuilder.clear()
                    remain = maxLengthFollowing
                    if (token.length <= remain) {
                        textBuilder.append(token)
                        remain -= token.length + 1
                    } else {
                        error("Token is too long - ${token.length}/$remain: $token")
                    }
                }
            }

            if (textBuilder.isNotEmpty()) {
                add(textBuilder.toString())
            }
        }
}

data class BreakOnLastContent(
    override val fallback: MessageBreakFallback = ThrowError,
) : MessageBreakStrategy {
    override fun splitText(text: String, messageSize: Int, maxLengthFirst: Int, maxLengthFollowing: Int) =
        if (messageSize <= maxLengthFollowing) {
            listOf("", text)
        } else {
            error("text to too long, max size $messageSize/$maxLengthFollowing: $text")
        }
}

data class SingleMessage(
    override val fallback: MessageBreakFallback = ThrowError,
) : MessageBreakStrategy {
    override fun splitText(text: String, messageSize: Int, maxLengthFirst: Int, maxLengthFollowing: Int) =
        error("text is too long, max size $messageSize/$maxLengthFirst: $text")
}
